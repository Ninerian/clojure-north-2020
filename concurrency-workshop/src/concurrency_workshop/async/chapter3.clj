(ns concurrency-workshop.async.chapter3
  (:require [clojure.core.async :as async]
            [clj-time.core :as cc]))

(comment

  ;; Merge
  ;; core.async has a very useful functionality to aid the
  ;; fan-out -> fan-in pattern i.e merge
  ;; The way it works is you can create a merge channel
  ;; which will receive messages from all the merged channels
  ;; until all of them close! This can be used to build interesting
  ;; functionality like the data processing pipeline which we will see
  ;; in the assignment

  (def in-channel-one   (async/chan))
  (def in-channel-two   (async/chan))
  (def in-channel-three (async/chan))

  ;; Question write a function which
  ;; writes given value to a channel continously
  ;; after sleeping a random amount of time
  (defn write-constantly
    "Constantly publishes the given value to the given channel in random
     intervals every 0-5 seconds."
    [channel publish-value]
    FIXME)

  (def merged (async/merge [in-channel-one
                            in-channel-two
                            in-channel-three]))

  ;; Question : Write a consumer for the merged channel
  ;; the consumption should stop once the channel is closed!
  (def merger FIXME)

  (defn trigger-merges
    []
    (write-constantly in-channel-one   "channel-one")
    (write-constantly in-channel-two   "channel-two")
    (write-constantly in-channel-three "channel-three"))

  (async/close! in-channel-three)

  ;; Mix
  ;; In most aspects merge and mix are the same but there are some
  ;; critical differences.
  ;;
  ;; It introduces an intermediary component - the mixer
  ;; It is configurable, you can add and remove input channels
  ;; Channels can be muted, paused and solo'ed on demand

  ;; :mute - keep taking from the input channel but discard any taken values
  ;; :pause - stop taking from the input channel
  ;; :solo - listen only to this (and other :soloed channels).

  ;; Lets start by creating channels for logs at
  ;; different levels
  (def debug-logger (async/chan))
  (def info-logger (async/chan))
  (def error-logger (async/chan))

  ;; Question write a function which
  ;; writes given log to a channel continously
  ;; after sleeping a random amount of time
  (defn log-constantly
    "Constantly publishes the given log on the channel
     at intervals every 0-5 seconds."
    [channel log]
    FIXME)

  ;; Lets create an output channel and a mixer
  ;; based on the channel
  (def log-dashboard (async/chan))
  (def log-mixer (async/mix log-dashboard))


  ;; Question : Lets write a consumer for the log
  ;; output channel and closes when channel is closed
  (defn get-logs
    []
    FIXME)

  (async/admix log-mixer debug-logger)
  (async/admix log-mixer info-logger)
  (async/admix log-mixer error-logger)

  (defn start-logging
    []
    (log-constantly debug-logger {:type :debug
                                  :msg (str "Debug log at " (cc/now)) })
    (log-constantly info-logger {:type :info
                                 :msg (str "Info log at " (cc/now)) })
    (log-constantly error-logger {:type :error
                                  :msg (str "Error log at " (cc/now)) }))

  ;; mute info logs for now : info will be taken but thrown away

  (async/toggle log-mixer {  info-logger {:mute true} })

  ;; pause the debug logs too : debug will not be taken from channel
  (async/toggle log-mixer {  debug-logger {:mute false
                                           :pause true} })

  ;; now only look at debug logs

  (async/toggle log-mixer {  debug-logger {:solo true
                                           :pause false} })


  ;; Pub-Sub
  ;; Lets create a simple message queue with core.async's
  ;; channels

  ;; The basic construct that we need is a publication
  ;; which is the entity that manages the input channel of the
  ;; message queue. To create it, we have to give it an input channel
  ;; and a routing function.
  ;; This routing function will be called on each incoming message
  ;; to decide which subscribers need to get that message.
  ;; Conceptually this is similar to a multi-method dispatch function

  (def input (async/chan))
  (def message-broker (async/pub input :topic))


  ;; To use this broker, we need to add subscribers to it.
  ;; adding a subscriber requires the publication, a possible value
  ;; returned by the routing function and the channel on which to forward
  ;; the message.
  ;; This looks like

  ;; publication(input channel) ---(routing-fn)--> output-chan1
  ;;                                           --> output-chan2


  ;; Lets create channels for our topics
  (def alerts (async/chan))     ;; routing value :alert
  (def audit-logs (async/chan)) ;; routing value :audit
  (def analytics (async/chan))  ;; routing value :analytics


  ;; Question Lets create our subcriptions and write the
  ;; go loops to read from these output channels

  (async/sub FIXME)
  (async/sub FIXME)
  (async/sub FIXME)

  (FIXME)
  (FIXME)
  (FIXME)


  (async/>!! input {:topic :alert :msg "This is an alert"})
  (async/>!! input {:topic :analytics :msg "This is an analytics event"})
  (async/>!! input {:topic :audit :msg "This is an audit log"})

  )
