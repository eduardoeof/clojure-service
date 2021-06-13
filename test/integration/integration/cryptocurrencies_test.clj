(ns integration.cryptocurrencies-test
  (:require [clojure.test :refer :all]
            [matcher-combinators.test :refer [match?]]
            [integration.util.mongodb :as util.mongodb]
            [clojure-service.controller :as controller]
            [clojure-service.component :as component]
            [integration.util.http :refer [http-post http-get json->edn edn->json]]))

(def components (atom nil))

(defn fixtures-once [test-case]
  (reset! components (component/create-and-start)) 
  (test-case)
  (reset! components nil))

(defn fixtures-each [test-case]
  (test-case)
  (util.mongodb/drop-collection "cryptocurrencies" (:mongodb @components)))

(use-fixtures :once fixtures-once)
(use-fixtures :each fixtures-each)

(def request-body {:name "Bitcoin"
                   :type "BTC"
                   :slug "bitcoin"
                   :quote {:USD {:price 9283.92
                                 :percent-change-1h -0.152774
                                 :percent-change-24h 0.518894
                                 :percent-change-7d 0.986573
                                 :last-updated "2018-08-09T22:53:32.000"}
                           :BTC {:price 1.0 
                                 :percent-change-1h 0.0 
                                 :percent-change-24h 0.0
                                 :percent-change-7d 0.0
                                 :last-updated "2018-08-09T22:53:32.000"
                                 :volume-24h 772012}}})

(def cryptocurrency (assoc request-body
                           :id string?
                           :created-at string?))

(deftest post-cryptocurrencies-test
  (testing "should create a cryptocurrency with success"
    (let [response (http-post "/api/cryptocurrencies" 
                              (edn->json request-body)
                              @components)]
      (is (match? {:status 201}
                  response))  
      (is (match? {:cryptocurrency cryptocurrency} 
                  (-> response :body json->edn)))))
  
  (testing "should responde bad request error when tried to create a cryptocurrency"
    (let [body (dissoc request-body :name)
          response (http-post "/api/cryptocurrencies" 
                              (edn->json body)
                              @components)]
      (is (match? {:status 400
                   :body (edn->json {:message "Request not valid"})}
                  response))))
  
  (testing "should responde internal server error when response doesn't match to a cryptocurrency"
    (binding [controller/create-cryptocurrency (fn [_] {})]
      (let [response (http-post "/api/cryptocurrencies" 
                                (edn->json request-body)
                                @components)]
        (is (match? {:status 500
                     :body (edn->json {:message "Internal server error"})}
                    response))))))

(deftest get-cryptocurrencies-test
  (testing "should get all saved cryptocurrencies"
    (http-post "/api/cryptocurrencies" (edn->json request-body) @components)

    (let [response (http-get "/api/cryptocurrencies" @components)]
      (is (match? {:status 200}
                  response))

      (is (match? {:cryptocurrencies [cryptocurrency]}
                  (-> response :body json->edn))))))

(deftest get-cryptocurrencies-by-id-test
  (testing "should get a cryptocurrency by id"
    (let [cryptocurrency-id (-> (http-post "/api/cryptocurrencies" (edn->json request-body) @components)
                                :body
                                json->edn
                                :cryptocurrency
                                :id)
          response (http-get (str "/api/cryptocurrencies/" cryptocurrency-id) @components)]
      (is (match? {:status 200}
                  response))     
      (is (match? {:cryptocurrency (merge {:id cryptocurrency-id}
                                          cryptocurrency)}
                  (-> response :body json->edn))))))

(deftest get-cryptocurrencies-internal-server-error-test
  (testing "should response internal server error when response doesn't match to a vector of cryptocurrency"
    (binding [controller/get-cryptocurrencies (fn [_] ["fake value"])]
      (let [response (http-get "/api/cryptocurrencies" @components)]
        (is (match? {:status 500
                     :body (edn->json {:message "Internal server error"})}
                    response))))))

(deftest get-cryptocurrencies-by-type-test
  (testing "given two different types of cryptocurrency (BTC and ETH)"
    (let [bitcoin  (edn->json request-body)
          ethereum (-> request-body
                       (assoc :name "Ethereum" 
                              :type "ETH"
                              :slug "ethereum")
                       edn->json)]

      (http-post "/api/cryptocurrencies" bitcoin @components)
      (http-post "/api/cryptocurrencies" ethereum @components))    

    (testing "when the endpoint GET /api/cryptocurrencies is requested by type BTC"
      (let [response (http-get "/api/cryptocurrencies?type=BTC" @components)]

        (testing "should only get the historic of BTC cryptocurrency"
          (is (match? {:status 200}
                      response)) 
          (is (match? {:cryptocurrencies [{:type "BTC"}]}
                      (-> response :body json->edn))))))

    (testing "when the endpoint GET /api/cryptocurrencies is requested by an unknown type"
      (let [response (http-get "/api/cryptocurrencies?type=XYZ" @components)]

        (testing "should get an empty collection"
          (is (empty? (-> response :body json->edn))))))))

