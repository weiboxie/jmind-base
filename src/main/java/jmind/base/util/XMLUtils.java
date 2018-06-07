package jmind.base.util;

import jmind.base.cache.CacheLoader;
import jmind.base.cache.DoubleCheckCache;
import jmind.base.cache.LoadingCache;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * xml 处理 @XmlRootElement
 * @author xieweibo
 * 详解：http://blog.csdn.net/yellowd1/article/details/49538995
 * https://blog.csdn.net/top_code/article/details/51660191
 */

public abstract class XMLUtils {

    private final static LoadingCache<Class<?>, JAXBContext> cache =
            new DoubleCheckCache<Class<?>, JAXBContext>(
                    new CacheLoader<Class<?>, JAXBContext>() {
                        @Override
                        public JAXBContext load(Class<?> clazz)  {
                            try {
                                return JAXBContext.newInstance(clazz);
                            } catch (JAXBException e) {
                                throw new RuntimeException(e.getMessage(), e);
                            }
                        }
                    });
    /**
     * 生成xml
     * @param t
     * @param isXmlFlagment 是否xml部分，fasle 会包含xml标签头
     * @param <T>
     * @return
     */
    public static <T> String toXML(T t, boolean isXmlFlagment) {
        try {
            JAXBContext jaxbContext = cache.get(t.getClass());
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, isXmlFlagment);
            //设置序列化的编码格式
            marshaller.setProperty(Marshaller.JAXB_ENCODING, GlobalConstants.UTF8);
            //设置格式化输出
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            StringWriter sw = new StringWriter();
            marshaller.marshal(t, sw);
            return sw.toString();
        } catch (JAXBException e) {
            System.err.println("parse xml err xml="+t.toString());
            throw new RuntimeException("Can't marshall to xml.", e);
        }
    }

    /**
     * xml文件配置转换为对象
   */


    public static <T> T fromXML(Class<T> t, String xml) {

        try {
            JAXBContext jaxbContext = cache.get(t);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            //jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, isXmlFlagment);

            StringReader sr = new StringReader(xml);
            XMLStreamReader xsr = XMLInputFactory.newInstance().createXMLStreamReader(sr);
            return jaxbUnmarshaller.unmarshal(xsr, t).getValue();
        } catch (Exception e) {
            System.err.println("Can't unmarshall to xml="+xml);
            throw new RuntimeException("Can't unmarshall to xml.", e);
        }
    }

}
