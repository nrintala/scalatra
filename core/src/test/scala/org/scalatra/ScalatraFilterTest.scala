package org.scalatra

import org.scalatest.matchers.ShouldMatchers
import test.scalatest.ScalatraSuite

/*
 * There are four types of servlet mappings: path, extension, default, and exact-match.  Test them all, as they can all
 * cause different splits of pathInfo vs. servletPath.
 */

class ScalatraFilterTestFilter extends ScalatraFilter {
  get("/path-mapped/filtered") {
    "filter"
  }

  get("/filtered.do") {
    "filter"
  }

  get("/filtered") {
    "filter"
  }

  get("/exact-match/filtered") {
    "filter"
  }

  get("/status-202") {
    status(202)
  }

  get("/init-param/:name") {
    initParameter(params("name")).toString
  }
}

class ScalatraFilterTestPathMappedServlet extends ScalatraServlet {
  get("/filtered") {
    "path-mapped"
  }

  get("/unfiltered") {
    "path-mapped"
  }
}

class ScalatraFilterTestExtensionMappedServlet extends ScalatraServlet {
  get("/filtered.do") {
    "extension-mapped"
  }

  get("/unfiltered.do") {
    "extension-mapped"
  }
}

class ScalatraFilterTestDefaultServlet extends ScalatraServlet {
  get("/filtered") {
    "default"
  }

  get("/unfiltered") {
    "default"
  }
}

class ScalatraFilterTestExactMatchServlet extends ScalatraServlet {
  get("/exact-match/filtered") {
    "exact match"
  }

  get("/exact-match/unfiltered") {
    "exact match"
  }
}

class ScalatraFilterTest extends ScalatraSuite with ShouldMatchers {
  val filterHolder = addFilter(classOf[ScalatraFilterTestFilter], "/*")
  filterHolder.setInitParameter("cat-who-is-biting-me", "Pete")

  // See SRV.11.2 of Servlet 2.5 spec for the gory details of servlet mappings
  addServlet(classOf[ScalatraFilterTestPathMappedServlet], "/path-mapped/*")
  addServlet(classOf[ScalatraFilterTestExtensionMappedServlet], "*.do")
  addServlet(classOf[ScalatraFilterTestDefaultServlet], "/")
  addServlet(classOf[ScalatraFilterTestExactMatchServlet], "/exact-match/filtered")
  addServlet(classOf[ScalatraFilterTestExactMatchServlet], "/exact-match/unfiltered")

  test("should filter matching request to path-mapped servlet") {
    get("/path-mapped/filtered") {
      body should equal("filter")
    }
  }

  test("should pass through unmatched request to path-mapped servlet") {
    get("/path-mapped/unfiltered") {
      body should equal("path-mapped")
    }
  }

  test("should filter matching request to extension-mapped servlet") {
    get("/filtered.do") {
      body should equal("filter")
    }
  }

  test("should pass through unmatched request to extension-mapped servlet") {
    get("/unfiltered.do") {
      body should equal("extension-mapped")
    }
  }

  test("should filter matching request to default servlet") {
    get("/filtered") {
      body should equal("filter")
    }
  }

  test("should pass through unmatched request to default servlet") {
    get("/unfiltered") {
      body should equal("default")
    }
  }

  test("should filter matching request to exact-match-mapped servlet") {
    get("/exact-match/filtered") {
      body should equal("filter")
    }
  }

  test("should pass through unmatched request to exact-match-mapped servlet") {
    get("/exact-match/unfiltered") {
      body should equal("exact match")
    }
  }

  test("init parameter returns Some if set") {
    get("/init-param/cat-who-is-biting-me") {
      body should equal ("Some(Pete)")
    }
  }

  test("init parameter returns None if not set") {
    get("/init-param/derp") {
      body should equal ("None")
    }
  }
}

class ScalatraFilterWithoutServletMappingTest 
  extends ScalatraSuite 
  with ShouldMatchers 
{
  addFilter(classOf[ScalatraFilterTestFilter], "/*")

  // Based on http://gist.github.com/519565, http://gist.github.com/519566.
  // Was instead giving a 404.
  test("should match even when no servlet is mapped") {
    get("/status-202") {
      status should equal (202)
    }
  }
}


