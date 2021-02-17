(defproject clojure-service "0.0.1-SNAPSHOT"
  :description "A simple clojure service"

  :url "https://github.com/eduardoeof/clojure-service"

  :dependencies [[org.clojure/clojure "1.10.1"]
                 [io.pedestal/pedestal.service "0.5.8"]
                 [io.pedestal/pedestal.jetty "0.5.8"]
                 [ch.qos.logback/logback-classic "1.2.3" :exclusions [org.slf4j/slf4j-api]]
                 [org.slf4j/jul-to-slf4j "1.7.26"]
                 [org.slf4j/jcl-over-slf4j "1.7.26"]
                 [org.slf4j/log4j-over-slf4j "1.7.26"]]

  :profiles {:dev {:aliases {"run-dev" ["trampoline" "run" "-m" "clojure-service.server/run-dev"]}
                   :dependencies [[io.pedestal/pedestal.service-tools "0.5.8"]
                                  [nubank/matcher-combinators "3.1.4"]]}

             :integration {:test-paths ^:replace ["test/integration"]}

             :uberjar {:aot [clojure-service.server]}}

  :aliases {"integration" ["with-profile" "+integration" "test"]}

  :test-paths ["test/integration"]

  :resource-paths ["config", "resources"]

  :main ^{:skip-aot true} clojure-service.server

  :min-lein-version "2.0.0")
