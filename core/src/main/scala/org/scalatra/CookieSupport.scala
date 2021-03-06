package org.scalatra

import javax.servlet.http.{HttpServletRequest, HttpServletResponse, Cookie}
import javax.servlet.ServletContext

case class CookieOptions(
        domain  : String  = "",
        path    : String  = "",
        maxAge  : Int     = -1,
        secure  : Boolean = false,
        comment : String  = "")


class RichCookies(cookieColl: Array[Cookie], response: HttpServletResponse) {
    def apply(key: String) = cookieColl.find(_.getName == key) match {
      case Some(cookie) => Some(cookie.getValue)
      case _ => None
    }
    def update(name: String, value: String, options: CookieOptions = CookieOptions()) = {
      val cookie = new Cookie(name, value)
      if (options.domain != null && options.domain.trim.length > 0) cookie.setDomain(options.domain)
      if (options.path != null && options.path.trim.length > 0) cookie.setPath(options.path)
      cookie.setMaxAge(options.maxAge)
      if(options.secure) cookie.setSecure(true)
      if(options.comment != null && options.comment.trim.length > 0) cookie.setComment(options.comment)

      response addCookie cookie
      cookie
    }
    def set(name: String, value: String, options: CookieOptions = CookieOptions()) = {
      this.update(name, value, options)
    }
  }

trait CookieSupport {

  self: ScalatraKernel =>

  protected implicit def cookieWrapper(cookieColl: Array[Cookie]) = new RichCookies(cookieColl, response)

  protected def cookies = request.getCookies match {
    case null => Array[Cookie]()
    case x => x
  }



}
