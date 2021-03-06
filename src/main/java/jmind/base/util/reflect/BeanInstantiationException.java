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

package jmind.base.util.reflect;

/**
 * 运行时实例化类异常
 *
 * @author xieweibo
 */
public class BeanInstantiationException extends RuntimeException {

  private Class beanClass;

  public BeanInstantiationException(Class beanClass, String msg) {
    this(beanClass, msg, null);
  }

  public BeanInstantiationException(Class beanClass, String msg, Throwable cause) {
    super("Could not instantiate bean class [" + beanClass.getName() + "]: " + msg, cause);
    this.beanClass = beanClass;
  }

  public Class getBeanClass() {
    return beanClass;
  }

}
