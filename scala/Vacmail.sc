import $ivy.`com.sun.mail:javax.mail:1.6.2`
import javax.mail._
import javax.mail.internet.MimeUtility
import javax.mail.internet.MailDateFormat
import java.util.Properties
import java.util.Date

val user = sys.env.get("MAIL_USER").get
val pass = sys.env.get("MAIL_SERVER_PASS").get
val imapHost = sys.env.get("MAIL_IMAP_HOST").get

val props = new Properties()
props.put("mail.imap.host", imapHost)

val sess = Session.getDefaultInstance(props)
val store = sess.getStore("imaps")
store.connect(imapHost, user, pass)
val folder = store.getFolder("vac")
folder.open(Folder.READ_ONLY)

val messages = folder.getMessages()
val mailDateFormat = new MailDateFormat()

// val headers = messages(0).getAllHeaders()
// while( headers.hasMoreElements()){
// 	println(headers.nextElement().getName())
// }
// for(m <- messages){
// 	val messageId = m.getHeader("Message-Id")(0)
// 	val inReplyTo = m.getHeader("In-Reply-To")
// 	val subject = m.getHeader("Subject")
// 	val date = m.getHeader("Date")
// 	println(messageId)
// 	println(date(0))
// 	println(if (inReplyTo == null) "null" else inReplyTo(0))
// 	println(if (subject == null) "" else MimeUtility.decodeText(subject(0)))
// 	println()
// }
val starters = messages
	.filter(_.getHeader("In-Reply-To") == null)
	.sortBy(_.getSentDate)
starters.foreach(printMessage)

def printMessage(m: Message): Unit = {
	println("----------")
	println(MimeUtility.decodeText(m.getHeader("From")(0)))
	println(m.getHeader("Message-Id")(0))
	println(m.getSentDate)
	println("**" + getSubject(m) + "**")
	// println(getBodyContent(m))
}

def getSubject(m: Message): String = {
	val header = m.getHeader("Subject")
	if( header != null && header.length != 0 ){
		MimeUtility.decodeText(header(0))
	} else {
		"(No Subject)"
	}
}

def getBodyContent(m: Message): String = {
	if( m.isMimeType("text/*") ){
		m.getContent().asInstanceOf[String]
	} else if( m.isMimeType("multipart/alternative") ){
		val mp = m.getContent().asInstanceOf[Multipart]
		var text = "(Failed to get content)"
		for(i <- 0 until mp.getCount()){
			val bp = mp.getBodyPart(i)
			if( bp.isMimeType("text/plain") ){
				val c = bp.getContent()
				if( c.isInstanceOf[String] ){
					text = c.asInstanceOf[String]
				}
			}
		}
		text
	} else {
		val contentType = m.getContentType()
		s"MimeType: ${contentType}"
	}
}

def cleanup() = {
	folder.close()
	store.close()
}



