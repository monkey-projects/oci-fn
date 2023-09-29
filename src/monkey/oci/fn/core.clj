(ns monkey.oci.fn.core
  "Core functionality for the OCI Fnproject API client.  The functions here
   mostly just delegate to Martian for performing the actual http calls."
  (:require [martian.core :as martian]
            [monkey.oci.common
             [martian :as cm]
             [utils :as u]]
            [schema.core :as s]))

(def json #{"application/json"})

(def application-routes
  [{:route-name :list-applications
    :method :get
    :path-parts ["/applications"]
    :query-schema {:compartmentId s/Str
                   (s/optional-key :displayName) s/Str
                   (s/optional-key :id) s/Str}
    :produces json}

   {:route-name :create-application
    :method :post
    :path-parts ["/applications"]
    :body-schema {:app {:compartmentId s/Str
                        :displayName s/Str
                        :subnetIds [s/Str]
                        (s/optional-key :config) {s/Str s/Str}
                        (s/optional-key :definedTags) {s/Str s/Any}
                        (s/optional-key :freeformTags) {s/Str s/Str}
                        (s/optional-key :shape) s/Str}}
    :consumes json
    :produces json}

   {:route-name :update-application
    :method :put
    :path-parts ["/applications/" :application-id]
    :path-schema {:application-id s/Str}
    :body-schema {:app {:config {s/Str s/Str}}}
    :consumes json
    :produces json}

   {:route-name :delete-application
    :method :delete
    :path-parts ["/applications/" :application-id]
    :path-schema {:application-id s/Str}
    :consumes json}])

(def routes (concat application-routes))

(def host (comp (partial format "https://functions.%s.oraclecloud.com/20181201") :region))

(defn make-context
  "Creates Martian context for the given configuration.  This context
   should be passed to subsequent requests."
  [conf]
  (cm/make-context conf host routes))

(def send-request martian/response-for)

(u/define-endpoints *ns* routes martian/response-for)
