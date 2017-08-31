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

package jmind.base.util;


/**
 * @author xieweibo
 */
public class Objects {

  public static boolean equal( Object a,  Object b) {
    return a == b || (a != null && a.equals(b));
  }

  public static int hashCode( Object... objects) {
    return java.util.Arrays.hashCode(objects);
  }

}
