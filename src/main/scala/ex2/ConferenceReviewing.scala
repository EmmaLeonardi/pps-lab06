package ex2

import ex2.Question.*

import scala.language.postfixOps

enum Question:
  case RELEVANCE, SIGNIFICANCE, CONFIDENCE, FINAL

trait ConferenceReviewing:
  /**
   * @param article the number of the article
   * @param scores  the scores of the questions
   *                loads a review for the specified article, with complete scores as a map
   */
  def loadReview(article: Int, scores: Map[Question, Int]): Unit

  /**
   * @param article      the number of the article
   * @param relevance    points, between 0 and 10
   * @param significance points, between 0 and 10
   * @param confidence   points, between 0 and 10
   * @param fin          points, between 0 and 10
   *                     loads a review for the specified article, with the 4 explicit scores
   */
  def loadReview(article: Int, relevance: Int, significance: Int, confidence: Int, fin: Int): Unit

  /**
   * @param article  the number of the article
   * @param question number
   * @return the scores given to the specified article and specified question, as an (ascending-ordered) list
   */
  def orderedScores(article: Int, question: Question): List[Int]

  /**
   * @param article the number of the article
   * @return the average score to question FINAL taken by the specified article
   */
  def averageFinalScore(article: Int): Double

  /**
   * An article is considered accepted if its averageFinalScore (not weighted) is > 5,
   * and at least one RELEVANCE score that is >= 8.
   *
   * @return the set of accepted articles
   */
  def acceptedArticles(): Set[Int]

  /**
   * @return accepted articles as a list of pairs article+averageFinalScore, ordered from worst to best based on averageFinalScore
   */
  def sortAcceptedArticles(): List[(Int, Double)]

  /**
   * @return a map from articles to their average "weighted final score", namely,
   *         the average value of CONFIDENCE*FINAL/10
   *         Note: this method is optional in this exam
   */
  def averageWeightedFinalScoreMap(): Map[Int, Double]

object ConferenceReviewing:
  def apply(): ConferenceReviewing = ConferenceReviewingImpl()

case class ConferenceReviewingImpl() extends ConferenceReviewing:

  private var conferenceReviews = List[(Int, Map[Question, Int])]()

  def loadReview(article: Int, scores: Map[Question, Int]): Unit =
    require(scores.size == Question.values.length)
    require(scores(Question.RELEVANCE)>= 0 && scores(Question.RELEVANCE) <= 10)
    require(scores(Question.SIGNIFICANCE)>= 0 && scores(Question.SIGNIFICANCE) <= 10)
    require(scores(Question.CONFIDENCE)>= 0 && scores(Question.CONFIDENCE) <= 10)
    require(scores(Question.FINAL)>= 0 && scores(Question.FINAL) <= 10)
    conferenceReviews = (article, scores) :: conferenceReviews

  def loadReview(article: Int, relevance: Int, significance: Int, confidence: Int, fin: Int): Unit =
    require(relevance >= 0 && relevance <= 10)
    require(significance >= 0 && significance <= 10)
    require(confidence >= 0 && confidence <= 10)
    require(fin >= 0 && fin <= 10)
    conferenceReviews = (article, Map[Question, Int](Question.RELEVANCE -> relevance, Question.SIGNIFICANCE -> significance, Question.CONFIDENCE -> confidence, Question.FINAL -> fin)) :: conferenceReviews

  def orderedScores(article: Int, question: Question): List[Int] = conferenceReviews.collect { case a if a._1 == article => a._2(question) }.sorted

  def averageFinalScore(article: Int): Double =
    val fin = conferenceReviews.collect { case a if a._1 == article => a._2(Question.FINAL) }
    if fin.isEmpty
    then 0
    else fin.sum * 1.0 / fin.length

  private def accepted(article: Int): Boolean =
    this.averageFinalScore(article) > 5 && conferenceReviews.collect { case a if a._1 == article => a._2 }.exists(a => a.contains(Question.RELEVANCE) && a(Question.RELEVANCE) >= 8)

  def acceptedArticles(): Set[Int] = conferenceReviews.map(a => a._1).distinct.filter(a => accepted(a)).toSet

  def sortAcceptedArticles(): List[(Int, Double)] = this.acceptedArticles().map(a => (a, this.averageFinalScore(a))).toList.sortBy(_._2)

  private def averageWeightedFinalScore(article: Int): Double =
    val total = conferenceReviews.collect { case a if a._1 == article => a._2(Question.FINAL) * a._2(Question.CONFIDENCE) / 10.0 }
    if total.isEmpty
    then 0
    else total.sum * 1.0 / total.length

  def averageWeightedFinalScoreMap(): Map[Int, Double] = conferenceReviews.map(a => a._1).distinct.map(a => (a, averageWeightedFinalScore(a))).toMap

  override def toString: String = conferenceReviews.toString()

@main def Main(): Unit =
  val confrev = ConferenceReviewing()
  val emptyConf = ConferenceReviewing()
  //No articles
  assert(confrev.toString == List().toString())
  assert(emptyConf.toString == confrev.toString)
  //Load articles
  confrev.loadReview(article = 1, relevance = 8, significance = 8, confidence = 6, fin = 8)
  val oneArticleLoaded = confrev.toString
  assert(oneArticleLoaded != List().toString())
  confrev.loadReview(article = 1, relevance = 2, significance = 2, confidence = 2, fin = 3)
  val map = Map(RELEVANCE -> 9, SIGNIFICANCE -> 9, CONFIDENCE -> 10, FINAL -> 9)
  confrev.loadReview(article = 2, map)
  assert(oneArticleLoaded != confrev.toString)
  //Ordered scores of articles
  assert(confrev.orderedScores(1, FINAL) == List(3, 8))
  assert(confrev.orderedScores(2, CONFIDENCE) == List(10))
  assert(emptyConf.orderedScores(1, RELEVANCE) == List())
  //Average final score
  val expected1 = (8 + 3) / 2.0
  val actual1 = confrev.averageFinalScore(1)
  assert(expected1 - 0.1 <= actual1 || actual1 >= expected1 + 0.1)
  val expected2 = 9.0
  val actual2 = confrev.averageFinalScore(2)
  assert(expected2 - 0.1 <= actual2 || actual2 >= expected2 + 0.1)
  assert(emptyConf.averageFinalScore(1)==0)
  //Accepted articles
  assert(confrev.acceptedArticles()==Set(1,2))
  assert(emptyConf.acceptedArticles()==Set())
  //Sort accepted articles
  assert(confrev.sortAcceptedArticles()==List((1,actual1), (2,actual2)))
  assert(emptyConf.sortAcceptedArticles()==List())
  //Average weighted final score map
  assert(confrev.averageWeightedFinalScoreMap()!=Map()) //article 1: (4,8+0,6)/2->2,7 article 2->9
  assert(emptyConf.averageWeightedFinalScoreMap()==Map())

