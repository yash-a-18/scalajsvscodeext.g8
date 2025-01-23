import org.scalatest._

import wordspec._
import matchers._
import scala.concurrent.Future


class FirstAsyncTest extends wordspec.AsyncWordSpec with should.Matchers{
  "this" should {
    "work" in {
      val fut = Future(3)
      fut.map(_ + 1).map(_ should be(4))
    }
  }
}
