//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-793 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.07.03 at 05:59:50 PM CEST 
//


package com.sun.jersey.json.impl.rim;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *         Metadata data type capable of having repository content associated with it as a repository item.
 *         Often used as base type for extended types defines by profiles of ebXML RegRep.
 *       
 * 
 * <p>Java class for ExtrinsicObjectType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ExtrinsicObjectType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:4.0}RegistryObjectType">
 *       &lt;sequence>
 *         &lt;element name="ContentVersionInfo" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:4.0}VersionInfoType" minOccurs="0"/>
 *         &lt;choice minOccurs="0">
 *           &lt;element name="RepositoryItemRef" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:4.0}SimpleLinkType"/>
 *           &lt;element name="RepositoryItem" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="mimeType" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:4.0}LongName" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExtrinsicObjectType", propOrder = {
    "contentVersionInfo",
    "repositoryItemRef",
    "repositoryItem"
})
@XmlSeeAlso({
    CommentType.class
})
public class ExtrinsicObjectType
    extends RegistryObjectType
{

    @XmlElement(name = "ContentVersionInfo")
    protected VersionInfoType contentVersionInfo;
    @XmlElement(name = "RepositoryItemRef")
    protected SimpleLinkType repositoryItemRef;
    @XmlElement(name = "RepositoryItem")
    @XmlMimeType("*/*")
    protected DataHandler repositoryItem;
    @XmlAttribute
    protected String mimeType;

    /**
     * Gets the value of the contentVersionInfo property.
     * 
     * @return
     *     possible object is
     *     {@link VersionInfoType }
     *     
     */
    public VersionInfoType getContentVersionInfo() {
        return contentVersionInfo;
    }

    /**
     * Sets the value of the contentVersionInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link VersionInfoType }
     *     
     */
    public void setContentVersionInfo(VersionInfoType value) {
        this.contentVersionInfo = value;
    }

    /**
     * Gets the value of the repositoryItemRef property.
     * 
     * @return
     *     possible object is
     *     {@link SimpleLinkType }
     *     
     */
    public SimpleLinkType getRepositoryItemRef() {
        return repositoryItemRef;
    }

    /**
     * Sets the value of the repositoryItemRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link SimpleLinkType }
     *     
     */
    public void setRepositoryItemRef(SimpleLinkType value) {
        this.repositoryItemRef = value;
    }

    /**
     * Gets the value of the repositoryItem property.
     * 
     * @return
     *     possible object is
     *     {@link DataHandler }
     *     
     */
    public DataHandler getRepositoryItem() {
        return repositoryItem;
    }

    /**
     * Sets the value of the repositoryItem property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataHandler }
     *     
     */
    public void setRepositoryItem(DataHandler value) {
        this.repositoryItem = value;
    }

    /**
     * Gets the value of the mimeType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Sets the value of the mimeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMimeType(String value) {
        this.mimeType = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ExtrinsicObjectType other = (ExtrinsicObjectType) obj;
        if (this.contentVersionInfo != other.contentVersionInfo && (this.contentVersionInfo == null || !this.contentVersionInfo.equals(other.contentVersionInfo))) {
            return false;
        }
        if (this.repositoryItemRef != other.repositoryItemRef && (this.repositoryItemRef == null || !this.repositoryItemRef.equals(other.repositoryItemRef))) {
            return false;
        }
//        if (this.repositoryItem != other.repositoryItem && (this.repositoryItem == null || !this.repositoryItem.equals(other.repositoryItem))) {
//            return false;
//        }
        if ((this.mimeType == null) ? (other.mimeType != null) : !this.mimeType.equals(other.mimeType)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.contentVersionInfo != null ? this.contentVersionInfo.hashCode() : 0);
        hash = 37 * hash + (this.repositoryItemRef != null ? this.repositoryItemRef.hashCode() : 0);
        //hash = 37 * hash + (this.repositoryItem != null ? this.repositoryItem.hashCode() : 0);
        hash = 37 * hash + (this.mimeType != null ? this.mimeType.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("{contentVersionInfo:%s, repositoryItemRef: %s, mimeType:%s}", contentVersionInfo, repositoryItemRef, mimeType);
    }

}
