package com.fsck.k9.mailstore


import java.io.ByteArrayInputStream

import com.fsck.k9.mail.internet.MimeBodyPart
import com.fsck.k9.mail.internet.MimeMessage
import com.fsck.k9.mail.internet.MimeMultipart
import org.junit.Test

import org.junit.Assert.*


class MimePartStreamParserTest {
    @Test
    fun innerMessage_DispositionInline() {
        val msg = MimePartStreamParser.parse(null, ByteArrayInputStream(("From: <x@example.org>\r\n" +
                "To: <y@example.org>\r\n" +
                "Subject: Testmail 1\r\n" +
                "Content-Type: multipart/mixed; boundary=1\n" +
                "\n" +
                "--1\n" +
                "Content-Type: text/plain\n" +
                "\n" +
                "some text in the first part\n" +
                "--1\n" +
                "Content-Type: message/rfc822; name=\"message\"\n" +
                "\n" +
                "To: <z@example.org>\n" +
                "Subject: Hi\n" +
                "Date: now\n" +
                "Content-Type: text/plain\n" +
                "\n" +
                "inner text\n" +
                "--1--").toByteArray()))

        val body = msg.body as MimeMultipart
        assertEquals(2, body.count.toLong())

        val messagePart = body.getBodyPart(1) as MimeBodyPart
        assertEquals("message/rfc822", messagePart.mimeType)
        assertTrue(messagePart.body is MimeMessage)
    }

    @Test
    fun innerMessage_dispositionAttachment() {
        val msg = MimePartStreamParser.parse(null, ByteArrayInputStream(("From: <x@example.org>\r\n" +
                "To: <y@example.org>\r\n" +
                "Subject: Testmail 2\r\n" +
                "Content-Type: multipart/mixed; boundary=1\n" +
                "\n" +
                "--1\n" +
                "Content-Type: text/plain\n" +
                "\n" +
                "some text in the first part\n" +
                "--1\n" +
                "Content-Type: message/rfc822; name=\"message\"\n" +
                "Content-Disposition: attachment\n" +
                "\n" +
                "To: <z@example.org>\n" +
                "Subject: Hi\n" +
                "Date: now\n" +
                "Content-Type: text/plain\n" +
                "\n" +
                "inner text\n" +
                "--1--").toByteArray()))

        val body = msg.body as MimeMultipart
        assertEquals(2, body.count.toLong())

        val messagePart = body.getBodyPart(1) as MimeBodyPart
        assertEquals("message/rfc822", messagePart.mimeType)
        assertTrue(messagePart.body is DeferredFileBody)
    }
}