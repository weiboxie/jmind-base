package jmind.base.util;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

/**
 * xml 处理
 * @author xieweibo
 *
 */
public abstract class XMLUtils {
    /**
     * 生成xml
     * @param t
     * @param isXmlFlagment 是否xml部分，fasle 会包含xml标签头
     * @param <T>
     * @return
     */
    public static <T> String toXML(T t, boolean isXmlFlagment) {
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(t.getClass());
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, isXmlFlagment);
            StringWriter sw = new StringWriter();
            jaxbMarshaller.marshal(t, sw);
            return sw.toString();
        } catch (JAXBException e) {
            throw new RuntimeException("Can't marshall to xml.", e);
        }
    }

    /**
     * xml文件配置转换为对象
   */


    public static <T> T fromXML(Class<T> t, String xml) {
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(t);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            //jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, isXmlFlagment);
            StringReader sr = new StringReader(xml);
            XMLStreamReader xsr = XMLInputFactory.newInstance().createXMLStreamReader(sr);
            return jaxbUnmarshaller.unmarshal(xsr, t).getValue();
        } catch (Exception e) {
            throw new RuntimeException("Can't unmarshall to xml.", e);
        }
    }

}
