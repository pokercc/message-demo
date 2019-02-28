package pokercc.java.message

import org.omg.CORBA.Object
import java.util.*

class Message(
    var what: Int,
    var obj: Object? = null,
    var runnable: Runnable? = null

) {
    lateinit var target: Handler

}

open class Handler(val looper: Looper = Looper.myLooper()!!) {
    open fun handleMessage(message: Message) {

    }

    fun send(message: Message) {
        message.target = this
        looper.messageQueue.enqueue(message)

    }
}

class Looper private constructor() {
    var messageQueue = MessageQueue()

    companion object {
        private val looperThreadLocal = ThreadLocal<Looper>()
        private var mainLooper: Looper? = null

        @JvmStatic
        fun myLooper(): Looper? {
            return looperThreadLocal.get()
        }

        @JvmStatic
        fun mainLooper(): Looper {
            return mainLooper!!

        }

        @JvmStatic
        fun prepare() {
            if (looperThreadLocal.get() == null) {
                looperThreadLocal.set(Looper())
            } else {
                throw IllegalStateException("looper exists in ${Thread.currentThread().name} thread")
            }

        }

        @JvmStatic
        fun loop() {
            val myLooper: Looper = myLooper() ?: throw IllegalStateException("looper not exists")
            while (true) {
                val message = myLooper.messageQueue.next()
                if (message != null) {
                    message.target.handleMessage(message)
                } else {
                    Thread.yield()
                }

            }
        }

        @JvmStatic
        fun prepareMainLooper() {
            if (mainLooper == null) {
                mainLooper = Looper()
                looperThreadLocal.set(mainLooper)
            } else {
                throw IllegalStateException("looper exists in main thread")
            }
        }
    }
}

class MessageQueue {
    private val queue: Deque<Message> = LinkedList<Message>()

    fun enqueue(message: Message) {
        queue.add(message)
    }

    fun next(): Message? {
        if (queue.size != 0) {
            return queue.poll()
        }
        return null
    }
}