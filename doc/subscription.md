update schema
```clojure
{:objects
 {:Ping
  {:description "Captures a message and a timestamp. This is used when testing Subscriptions."
   :fields      {:message   {:type (non-null String)}
                 :timestamp {:type (non-null String)}}}}

 :subscriptions
 {:ping
  {:type        :Ping
   :description "Sends the provided message a particular number of times, as a subscription."
   :stream      :stream-ping
   :args
                {:count   {:type    Int
                           :default 5}
                 :message {:type String}}}}} 
```

attach streamer fn `(util/attach-streamers schema {:stream-ping stream-ping})`

A streamer is passed three values:

    The application context
    The field arguments
    The source stream callback

As new values are published, the streamer must pass those values to the source stream callback.

The subscription stays active until either the client closes the connection, or until `nil` is passed to the source stream callback.

Further, the streamer must return a function to clean up the stream when the subscription is terminated.

```clojure
(def *ping-subscribes (atom 0))
(def *ping-cleanups (atom 0))

(defn stream-ping
  [context {:keys [message count]} source-stream]
  (swap! *ping-subscribes inc)
  (let [runnable ^Runnable (fn []
                             (dotimes [i count]
                               (Thread/sleep 500)
                               (source-stream {:message   (str message " #" (inc i))
                                               :timestamp (System/currentTimeMillis)}))
                             ; close the connection 
                             (source-stream nil))]
    (.start (Thread. runnable "stream-ping-thread")))
  ; return a cleanup fn
  #(swap! *ping-cleanups inc))
```

test
```
subscription {
  ping(count: 10, message: "test") {
    message
    timestamp
  }
}
```

Lacinia-Pedestal uses WebSockets to create a durable connection between the client and the server, protocol follow this https://github.com/apollographql/subscriptions-transport-ws/blob/master/PROTOCOL.md.
