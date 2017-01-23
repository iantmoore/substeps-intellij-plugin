package uk.co.itmoore.intellisubsteps

import scala.collection.JavaConverters._
/**
  * Created by ian on 23/01/17.
  */
object Utils {

  def allExpressionsMatch(text: String, jSet : java.util.Set[String]) = {

    val theSet = jSet.asScala


    val results =
    theSet.forall(s => {

      val r = s.r
      r.findFirstMatchIn(text).isDefined

    })
    results
  }

}
