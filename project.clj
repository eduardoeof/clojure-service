(defproject clojure-service "0.0.1-SNAPSHOT"
  :description "A simple clojure service"

  :url "https://github.com/eduardoeof/clojure-service"

  :dependencies [[org.clojure/clojure "1.10.1"]
                 [io.pedestal/pedestal.service "0.5.8"]
                 [io.pedestal/pedestal.jetty "0.5.8"]
                 [ch.qos.logback/logback-classic "1.2.3" :exclusions [org.slf4j/slf4j-api]]
                 [org.slf4j/jul-to-slf4j "1.7.26"]
                 [org.slf4j/jcl-over-slf4j "1.7.26"]
                 [org.slf4j/log4j-over-slf4j "1.7.26"]
                 [clj-time "0.15.2"]]

  :profiles {:dev {:dependencies [[io.pedestal/pedestal.service-tools "0.5.8"]
                                  [nubank/matcher-combinators "3.1.4"]
                                  [org.clojure/data.json "1.0.0"]]
                   :plugins [[com.jakemccrary/lein-test-refresh "0.24.1"]]
                   :aliases {"run-dev" ["trampoline" "run" "-m" "clojure-service.server/run-dev"]} }

             :unit {:test-paths ^:replace ["test/unit"]}
             :integration {:test-paths ^:replace ["test/integration"]}

             :uberjar {:aot [clojure-service.server]}}

  :aliases {"integration" ["with-profile" "+integration" "test"]
            "unit"        ["with-profile" "+unit" "test"]}

  :test-paths ["test/integration" "test/unit"]

  :resource-paths ["config", "resources"]

  :main ^{:skip-aot true} clojure-service.server

  :min-lein-version "2.0.0")
