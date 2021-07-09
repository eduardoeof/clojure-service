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
                                 :last-updated "2018-08-09T22:53:32.000Z"}
                           :BTC {:price 1.0 
                                 :percent-change-1h 0.0 
                                 :percent-change-24h 0.0
                                 :percent-change-7d 0.0
                                 :last-updated "2018-08-09T22:53:32.000Z"
                                 :volume-24h 772012}}})

(def cryptocurrency (assoc request-body
                           :id string?
                           :created-at string?))

(deftest post-cryptocurrencies-test
  (testing "when the POST /api/crytocurrencies endpoint is requested with a cryptocurrency"
    (let [response (http-post "/api/cryptocurrencies" 
                              (edn->json request-body)
                              @components)]

      (testing "then it should return 201 and the cryptocurrency created"
        (is (match? {:status 201}
                    response))  
        (is (match? {:cryptocurrency cryptocurrency} 
                    (-> response :body json->edn))))))
  
  (testing "when the POST /api/crytocurrencies endpoint is requested with a cryptocurrency without all required attributes"
    (let [body (dissoc request-body :name)
          response (http-post "/api/cryptocurrencies" 
                              (edn->json body)
                              @components)]

      (testing "then it should return 400 (bad request) and a message"
        (is (match? {:status 400
                     :body (edn->json {:message "Request not valid"})}
                    response)))))
  
  (testing "when the POST /api/crytocurrencies endpoint is requested but the service tries to return a non valid cryptocurrency"
    (binding [controller/create-cryptocurrency (fn [_] {})]
      (let [response (http-post "/api/cryptocurrencies" 
                                (edn->json request-body)
                                @components)]

        (testing "then it should return 500 (internal server error) because response doesn't follow the expected response schema"
          (is (match? {:status 500
                       :body (edn->json {:message "Internal server error"})}
                      response)))))))

(deftest get-cryptocurrencies-test
  (testing "given a cryptocurrency previews created"
    (http-post "/api/cryptocurrencies" (edn->json request-body) @components)

    (testing "when the endpoint GET /api/cryptocurrencies is requested"
      (let [response (http-get "/api/cryptocurrencies" @components)]

        (testing "then it should return 200 and all storaged cryptocurrencies"
          (is (match? {:status 200}
                      response))

          (is (match? {:cryptocurrencies [cryptocurrency]}
                      (-> response :body json->edn))))))))

(deftest get-cryptocurrencies-by-id-test
  (testing "given a cryptocurrency previews created")
    (let [cryptocurrency (-> (http-post "/api/cryptocurrencies" (edn->json request-body) @components)
                                :body
                                json->edn
                                :cryptocurrency)]

      (testing "when the endpoint GET /api/cryptocurrencies is requested with a cryptocurrency id"
        (let [response (http-get (str "/api/cryptocurrencies/" (:id cryptocurrency)) @components)]
  
          (testing "it should return 200 and the cryptocurrency of the passed id"
            (is (match? {:status 200}
                        response))     

            (is (match? {:cryptocurrencies [(assoc cryptocurrency :id (:id cryptocurrency))]}
                        (-> response :body json->edn))))))))

(deftest get-cryptocurrencies-internal-server-error-test
  (testing "when the GET /api/crytocurrencies endpoint is requested but the service tries to return a not expected response"
    (binding [controller/get-cryptocurrencies (fn [_] ["fake value"])]
      (let [response (http-get "/api/cryptocurrencies" @components)]

        (testing "then it should return 500 and a error message"
          (is (match? {:status 500
                       :body (edn->json {:message "Internal server error"})}
                      response)))))))

(deftest get-cryptocurrencies-by-type-test
  (testing "given two different types of cryptocurrency (BTC and ETH) previews created"
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

        (testing "then it should only get the historic of BTC cryptocurrency"
          (is (match? {:status 200}
                      response)) 
          (is (match? {:cryptocurrencies [{:type "BTC"}]}
                      (-> response :body json->edn))))))

    (testing "when the endpoint GET /api/cryptocurrencies is requested by an unknown type"
      (let [response (http-get "/api/cryptocurrencies?type=XYZ" @components)]

        (testing "then it should get an empty collection"
          (is (match? {:cryptocurrencies empty?}
                      (-> response :body json->edn))))))))

(deftest get-cryptocurrencies-by-date-range
  (testing "given three cryptocurrencies records with same type and from different datetime"
    (testing "when the endpoint GET /api/cryptocurrencies is requested by type and a date range"
      (testing "then it should respond only cryptocurrencies records in the date range"))

    (testing "when the endpoint GET /api/cryptocurrencies is requested by a date range that doesn't have records"
      (testing "then it should respond an empty collection"))))

