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
                        (s/optional-key :config) s/Any
                        (s/optional-key :definedTags) s/Any
                        (s/optional-key :freeformTags) s/Any
                        (s/optional-key :shape) s/Str}}
    :consumes json
    :produces json}

   {:route-name :update-application
    :method :put
    :path-parts ["/applications/" :application-id]
    :path-schema {:application-id s/Str}
    :body-schema {:app {:config s/Any}}
    :consumes json
    :produces json}

   {:route-name :delete-application
    :method :delete
    :path-parts ["/applications/" :application-id]
    :path-schema {:application-id s/Str}
    :consumes json}

   {:route-name :get-application
    :method :get
    :path-parts ["/applications/" :application-id]
    :path-schema {:application-id s/Str}
    :consumes json}])

(def update-fn-details
  {:displayName s/Str
   :memoryInMBs s/Int
   (s/optional-key :config) s/Any
   (s/optional-key :definedTags) s/Any
   (s/optional-key :freeformTags) s/Any
   (s/optional-key :image) s/Str
   (s/optional-key :timeoutInSeconds) s/Int})

(def function-routes
  [{:route-name :list-functions
    :method :get
    :path-parts ["/functions"]
    :query-schema {:applicationId s/Str
                   (s/optional-key :displayName) s/Str
                   (s/optional-key :id) s/Str}
    :produces json}

   {:route-name :get-function
    :method :get
    :path-parts ["/functions/" :function-id]
    :path-schema {:function-id s/Str}
    :produces json}

   {:route-name :create-function
    :method :post
    :path-parts ["/functions"]
    :body-schema {:fn (assoc update-fn-details
                             :applicationId s/Str)}
    :consumes json
    :produces json}
   
   {:route-name :update-function
    :method :put
    :path-parts ["/functions/" :function-id]
    :path-schema {:function-id s/Str}
    :body-schema {:fn update-fn-details}
    :consumes json
    :produces json}

   {:route-name :delete-function
    :method :delete
    :path-parts ["/functions/" :function-id]
    :path-schema {:function-id s/Str}
    :consumes json}

   {:route-name :invoke-function
    :method :post
    :path-parts ["/functions/" :function-id "/actions/invoke"]
    :path-schema {:function-id s/Str}
    :body-schema {:body s/Str}
    :header-schema {(s/optional-key :fn-invoke-type) (s/enum :sync :detached)
                    (s/optional-key :fn-intent) s/Str}
    :produces json}])

(def routes (concat application-routes
                    function-routes))

(def host (comp (partial format "https://functions.%s.oraclecloud.com/20181201") :region))

(defn make-context
  "Creates Martian context for the given configuration.  This context
   should be passed to subsequent requests."
  [conf]
  (cm/make-context conf host routes))

(def send-request martian/response-for)

(u/define-endpoints *ns* routes martian/response-for)
