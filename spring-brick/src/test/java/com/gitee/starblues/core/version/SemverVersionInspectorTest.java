/**
 * Copyright [2019-Present] [starBlues]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.gitee.starblues.core.version;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.powermock.api.mockito.PowerMockito.*;

/**
 * SemverVersionInspectorTest 单元测试
 *
 * @author starBlues
 * @version 3.0.0
 */
@RunWith(PowerMockRunner.class)
public class SemverVersionInspectorTest extends TestCase {

    private SemverVersionInspector versionInspector;


    @Before
    public void setUp(){
        versionInspector = spy(new SemverVersionInspector());
    }

    @Test
    public void test_equal(){
        assertEquals(0, versionInspector.compareTo("1.0.0", "1.0.0"));
        assertEquals(0, versionInspector.compareTo("1.0.0-SNAPSHOT", "1.0.0-SNAPSHOT"));
    }

    @Test
    public void test_greater(){
        assertTrue(versionInspector.compareTo("2.0.0", "1.0.0") > 0);
        assertTrue(versionInspector.compareTo("2.0.0-SNAPSHOT", "1.0.0-SNAPSHOT") > 0);
        assertTrue(versionInspector.compareTo("2.0.2", "1.2.0") > 0);
        assertTrue(versionInspector.compareTo("1.2.2", "1.2.0") > 0);
    }

    @Test
    public void test_less(){
        assertTrue(versionInspector.compareTo("1.0.0", "2.0.0") <= 0);
        assertTrue(versionInspector.compareTo("1.0.0-SNAPSHOT", "2.0.0-SNAPSHOT") <= 0);
    }


}