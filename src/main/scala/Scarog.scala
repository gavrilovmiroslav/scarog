
package scarog

import scarog.utils._

import org.lwjgl.glfw._
import org.lwjgl.opengl._;
import org.lwjgl.opengl.GL11._;
import org.lwjgl.glfw.GLFW._
import org.lwjgl.system.MemoryStack._

object Main {
  def main(args: Seq[String]) = Scarog.run
}

object Scarog {
  type GLFWWindowContext = Long

  val actors = scala.collection.mutable.Seq[Actor]()

  def run = {
    println("Scarog is awake!")

    Scarog.init match {
      case Ok(window) =>
        println(s"Window reference [${window}] received!")
        println(Scarog.loop(window).get)

      case Error(message) =>
        println(s"Error: ${message}, killing process.")
        throw new IllegalStateException(message)
    }

    println("Scarog is going to sleep!")
  }

  def init(): Log[GLFWWindowContext] = {
    GLFWErrorCallback.createPrint(null).free()
    if(!glfwInit())
      return Error("Unable to initialize GLFW")

    glfwDefaultWindowHints()
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)

    val window = glfwCreateWindow(800, 600, "Scarog", 0, 0)
    if(window == 0) return Error("Failed to create the GLFW window")

    glfwSetKeyCallback(window, new GLFWKeyCallback() {
      override def invoke(window: Long, key: Int, scancode: Int, action: Int, mods: Int): Unit = {
        if(key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
          glfwSetWindowShouldClose(window, true)
      }
    })

    val stack = stackPush()
    val pWidth = stack.mallocInt(1)
    val pHeight = stack.mallocInt(1)
    glfwGetWindowSize(window, pWidth, pHeight)

    val videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor())
    glfwSetWindowPos(window,
      (videoMode.width() - pWidth.get(0)) / 2,
      (videoMode.height() - pHeight.get(0)) / 2)
    stack.pop()

    glfwMakeContextCurrent(window)
    glfwSwapInterval(1)
    glfwShowWindow(window)

    Ok(window)
  }

  def loop(window: GLFWWindowContext): Log[String] = {
    GL.createCapabilities()
    glClearColor(0, 0, 0, 0)

    var tickRate: Long = 1000000000 / 60
    var gameTime = System.nanoTime()
    var frameCounter = 0

    try {
      while (!glfwWindowShouldClose(window)) {
        var currentTime = System.nanoTime()
        if(currentTime - gameTime > 33 * 1000)
          EventBus.post(new TickTimeEvent)

        if(currentTime > gameTime + tickRate * 2)
          gameTime = currentTime
        else
          gameTime += tickRate

        while(System.nanoTime() < gameTime) Thread.`yield`()


        // player stuff here
        // consume key
        // else others act
      }
    } catch {
      case e => return Error(e.getMessage)
    }

    Ok("Finished main loop.")
  }
}
