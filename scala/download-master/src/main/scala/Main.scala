package main

import scala.collection.JavaConversions._
import com.gargoylesoftware.htmlunit._
import com.gargoylesoftware.htmlunit.html._
import java.nio.file.{Files, Path}

object Main {

  val startPage = "http://www.iryohoken.go.jp/shinryohoshu/downloadMenu/"
  val masters = List(
    ("医科診療行為マスター", "s.zip"),
    ("医薬品マスター", "y.zip"),
    ("特定器材マスター", "t.zip"),
    ("傷病名マスター", "b.zip"),
    ("修飾語マスター", "z.zip"),
  )

  def main(args: Array[String]) {
    val webClient = new WebClient
    val page = webClient.getPage(startPage).asInstanceOf[HtmlPage]
    for((anch, file) <- masters){
      println(s"downloading ${file}")
      val anchor = page.getAnchorByText(anch)
      val downloadPage = anchor.click().asInstanceOf[Page]
      val webResponse = downloadPage.getWebResponse()
      val ins = webResponse.getContentAsStream()
      val out = Files.newOutputStream(Path.of(s"../../work/${file}"))
      ins.transferTo(out)
      out.close()
      ins.close()
    }
    println("Masters donwloaded to ../../work/")
  }
}

