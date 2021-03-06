/*
 *  
 *
 * The jmind-pigg Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package jmind.base.util.bean;


import jmind.base.cache.CacheLoader;
import jmind.base.cache.DoubleCheckCache;
import jmind.base.cache.LoadingCache;
import jmind.base.util.DataUtil;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author xieweibo
 */
public class BeanUtil {

  private final static LoadingCache<Class<?>, Map<String,PropertyMeta>> cache =
      new DoubleCheckCache<Class<?>, Map<String,PropertyMeta>>(
      new CacheLoader<Class<?>, Map<String,PropertyMeta>>() {
        @Override
        public Map<String,PropertyMeta> load(Class<?> clazz) {
          try {
            BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
           // Field[] fields = clazz.getDeclaredFields();
            Map<String, PropertyMeta> metaMap = new HashMap<>();
            for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
              Method readMethod = pd.getReadMethod();
              Method writeMethod = pd.getWriteMethod();
              if (readMethod != null && writeMethod != null) {
                String name = pd.getName();
                Type type = readMethod.getGenericReturnType(); // 和writeMethod的type相同
                Field field = tryGetField(clazz, name);
                if (isBoolean(pd.getPropertyType()) && field == null) {
                  String bname = "is" + DataUtil.firstLetterToUpperCase(name);
                  field = tryGetField(clazz, bname);
                  if (field != null) {
                    name = bname;  // 使用isXxYy替换xxYy
                  }
                }
                PropertyMeta meta = new PropertyMeta(name, type, readMethod, writeMethod,
                    methodAnnos(readMethod), methodAnnos(writeMethod), fieldAnnos(field));
                metaMap.put(name, meta);
              }
            }
            return Collections.unmodifiableMap(metaMap);
          } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
          }
        }
      });

  public static Collection<PropertyMeta> fetchPropertyMetas(Class<?> clazz) {
    return cache.get(clazz).values();
  }

  public static Map<String,PropertyMeta> getPropertyMeta(Class<?> clazz) {
    return cache.get(clazz);
  }

  private static boolean isBoolean(Class<?> clazz) {
    return boolean.class.equals(clazz) || Boolean.class.equals(clazz);
  }




  private static Field tryGetField(Class<?> clazz, String name) {
    if(clazz==null || Object.class.equals(clazz)){
      return null;
    }
    try {
      Field field = clazz.getDeclaredField(name);
      return  field;
    } catch (Exception e) {
      // ignore
      return tryGetField(clazz.getSuperclass(),name);
    }
  }

  private static Set<Annotation> methodAnnos(Method m) {
    Set<Annotation> annos = new HashSet<Annotation>();
    for (Annotation anno : m.getAnnotations()) {

      annos.add(anno);
    }
    return annos;
  }

  private static Set<Annotation> fieldAnnos(Field f) {
    Set<Annotation> annos = new HashSet<Annotation>();
    if (f != null) {
      for (Annotation anno : f.getAnnotations()) {
        annos.add(anno);
      }
    }
    return annos;
  }

  private static List<PropertyMeta> transToList(TreeMap<Integer, PropertyMeta> metaMap) {
    List<PropertyMeta> metas = new ArrayList<PropertyMeta>();
    for (Integer key : metaMap.keySet()) {
      metas.add(metaMap.get(key));
    }
    return Collections.unmodifiableList(metas);
  }

}
