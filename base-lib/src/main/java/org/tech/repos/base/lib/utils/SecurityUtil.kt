package org.tech.repos.base.lib.utils

import android.content.Context
import android.net.http.SslCertificate
import android.net.http.SslError
import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.security.MessageDigest
import java.security.cert.Certificate
import java.security.cert.CertificateFactory

/**
 * Author: xuan
 * Created on 2021/7/13 11:18.
 *
 * Describe:
 */
object SecurityUtil {

    fun checkSslCer(sslCerHex: String, ssl: SslError, baseHost: String): Boolean {
        val host = ssl.certificate.issuedTo.cName
        return if (host != null && host.contains(baseHost)) {
            val hex = getSSLCertFromServer(ssl.certificate)
            sslCerHex.equals(hex, true)
        } else {
            true
        }
    }

    fun getSSLCertSHA256FromCert(inputStream: InputStream): String? {
        try {
            val context: Context = AppGlobals.application!!.applicationContext
            val caInput: InputStream = BufferedInputStream(inputStream)
            val cf = CertificateFactory.getInstance("X.509")
            val ca = cf.generateCertificate(caInput)
            val sha256 = MessageDigest.getInstance("SHA-256")
            val key = sha256.digest(ca.encoded)
            return ConvertUtils.bytes2HexString(key);
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun getSSLCertFromServer(cert: SslCertificate?): String? {
        try {
            val bundle = SslCertificate.saveState(cert)
            val bytes = bundle.getByteArray("x509-certificate")
            val cf = CertificateFactory.getInstance("X.509")
            val ca: Certificate = cf.generateCertificate(ByteArrayInputStream(bytes))
            val sha256 = MessageDigest.getInstance("SHA-256")
            val key = sha256.digest(ca.encoded)
            return ConvertUtils.bytes2HexString(key)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}