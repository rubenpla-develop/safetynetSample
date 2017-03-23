package com.rubenpla.develop.safetynetsample.model;

import java.util.List;

/**
 * Created by alten on 21/3/17.
 */
public class JWS {
    private String nonce;
    private long timestampMs;
    private String apkPackageName;
    private String apkDigestSha256;
    private boolean ctsProfileMatch;
    private String extension;
    private List<String> apkCertificateDigestSha256;
    private boolean basicIntegrity;

    /**
     * Gets nonce.
     *
     * @return the nonce
     */
    public String getNonce() {
        return nonce;
    }

    /**
     * Gets timestamp ms.
     *
     * @return the timestamp ms
     */
    public long getTimestampMs() {
        return timestampMs;
    }

    /**
     * Gets apk package name.
     *
     * @return the apk package name
     */
    public String getApkPackageName() {
        return apkPackageName;
    }

    /**
     * Gets apk digest sha 256.
     *
     * @return the apk digest sha 256
     */
    public String getApkDigestSha256() {
        return apkDigestSha256;
    }

    /**
     * Is cts profile match boolean.
     *
     * @return the boolean
     */
    public boolean isCtsProfileMatch() {
        return ctsProfileMatch;
    }

    /**
     * Gets extension.
     *
     * @return the extension
     */
    public String getExtension() {
        return extension;
    }

    /**
     * Gets apk certificate digest sha 256.
     *
     * @return the apk certificate digest sha 256
     */
    public List<String> getApkCertificateDigestSha256() {
        return apkCertificateDigestSha256;
    }

    /**
     * Is basic integrity boolean.
     *
     * @return the boolean
     */
    public boolean isBasicIntegrity() {
        return basicIntegrity;
    }
}


