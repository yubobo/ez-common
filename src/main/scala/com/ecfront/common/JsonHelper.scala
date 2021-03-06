package com.ecfront.common

import java.util.TimeZone

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.node.{ArrayNode, ObjectNode}
import com.fasterxml.jackson.databind.{DeserializationFeature, JsonNode, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import com.typesafe.scalalogging.slf4j.LazyLogging

/**
  * Scala版本的Json辅助类<br/>
  * 使用<i>jackson-module-scala</i>封装
  */
object JsonHelper extends LazyLogging {

  private val mapper = new ObjectMapper() with ScalaObjectMapper
  mapper.registerModule(DefaultScalaModule)
  mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
  mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true)
  mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)

  def setTimeZone(tz: TimeZone): Unit = {
    mapper.setTimeZone(tz)
  }

  /**
    * object 转 json字符串
    *
    * @param obj object
    * @return json字符串
    */
  def toJsonString(obj: Any): String = {
    mapper.writeValueAsString(obj)
  }

  /**
    * object 转 json
    *
    * @param obj object
    * @return json
    */
  def toJson(obj: Any): JsonNode = {
    obj match {
      case o: String => mapper.readTree(o)
      case _ => mapper.valueToTree(obj)
    }
  }

  /**
    * json或string 转 object
    *
    * @param obj json或string
    * @return object
    */
  def toObject[E](obj: Any, clazz: Class[E]): E = {
    try {
      obj match {
        case o: String =>
          clazz match {
            case c if c == classOf[String] =>
              o.asInstanceOf[E]
            case c if c == classOf[Int] =>
              o.toInt.asInstanceOf[E]
            case c if c == classOf[Long] =>
              o.toLong.asInstanceOf[E]
            case c if c == classOf[Double] =>
              o.toDouble.asInstanceOf[E]
            case c if c == classOf[Float] =>
              o.toFloat.asInstanceOf[E]
            case c if c == classOf[Boolean] =>
              o.toBoolean.asInstanceOf[E]
            case c if c == classOf[Byte] =>
              o.toByte.asInstanceOf[E]
            case c if c == classOf[Short] =>
              o.toShort.asInstanceOf[E]
            case c if c == classOf[Void] =>
              null.asInstanceOf[E]
            case _ =>
              mapper.readValue[E](o, clazz)
          }
        case o: JsonNode => mapper.readValue(o.toString, clazz)
        case _ => mapper.readValue(mapper.writeValueAsString(obj), clazz)
      }
    } catch {
      case e: Throwable =>
        logger.error(s"Parsing to [${clazz.getName}] error , source is :${obj.toString}")
        throw e
    }
  }

  /**
    * json或string 转 generic object
    */
  def toObject[E](obj: Any)(implicit m: Manifest[E]): E = {
    try {
      obj match {
        case o: String => mapper.readValue[E](o)
        case o: JsonNode => mapper.readValue[E](o.toString)
        case _ => mapper.readValue[E](mapper.writeValueAsString(obj))
      }
    } catch {
      case e: Throwable =>
        logger.error(s"Parsing to [${m.toString()}] error , source is :${obj.toString}")
        throw e
    }
  }

  def createObjectNode(): ObjectNode = {
    mapper.createObjectNode()
  }

  def createArrayNode(): ArrayNode = {
    mapper.createArrayNode()
  }

  def getMapper: ObjectMapper = {
    mapper
  }

}


