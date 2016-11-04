/**
 * The MIT License
 * Copyright (c) ${project.inceptionYear} the-james-burton
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.jimsey.projects.turbine.inspector.matchers.library;

import org.apache.commons.lang3.math.NumberUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

public class IsNumeric extends BaseMatcher<String> {

  private static final IsNumeric INSTANCE = new IsNumeric();

  @Override
  public boolean matches(Object item) {
    return item != null && item instanceof String && NumberUtils.isNumber(((String) item));
  }

  @Override
  public void describeTo(Description description) {
    description.appendText("a numeric string");
  }

  /**
   * Creates a matcher of {@link String} that matches when NumberUtils.isNumber() is true for the examined string
   * <p/>
   * For example:
   * <pre>assertThat("123", isNumeric())</pre>
   */
  @Factory
  public static Matcher<String> isNumeric() {
      return INSTANCE;
  }

}
