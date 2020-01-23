
package scarog

object EventBus {
  val listenersFor = Map[Class[_], Seq[Actor]]()

  def post[T](message: T) =
    listenersFor.get(message.getClass) match {
      case Some(listeners) =>
        listeners.foreach(_.receive(message))
      case None =>
    }
}

trait Actor {
  def receive(message: Any): Unit
}

class TickTimeEvent()
