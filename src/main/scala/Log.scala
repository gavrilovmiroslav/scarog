
package scarog.utils

sealed trait Log[T] { self =>
  def get = self match {
    case Ok(instance) => instance
    case Error(message) => throw new Exception(message)
  }
}

case class Ok[T](instance: T) extends Log[T]
case class Error[T](message: String) extends Log[T]
