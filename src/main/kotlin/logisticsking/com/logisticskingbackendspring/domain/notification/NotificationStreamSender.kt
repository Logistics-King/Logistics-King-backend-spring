package logisticsking.com.logisticskingbackendspring.domain.notification

interface NotificationStreamSender {
    fun send(notification: Notification)

    fun sendAll(notifications: List<Notification>) {
        notifications.forEach(::send)
    }
}
