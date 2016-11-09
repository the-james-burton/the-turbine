/**
 * The MIT License
 * Copyright (c) 2015 the-james-burton
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
package org.jimsey.projects.turbine.inspector.junit;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

import org.junit.Assert;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

/**
 * ORIGINAL VERSION : Copyright 2008-2013 smartics, Kronseder & Reiner GmbH
 * https://www.smartics.eu/confluence/display/BLOG/2013/02/26/Testing+equals,+hashCode,+et+al.+in+Java
 * 
 * Modified to remove generics and use Comparable directly due to...
 * https://github.com/junit-team/junit4/blob/master/doc/ReleaseNotes4.12.md
 * Pull request #572: Ensuring no-generic-type-parms validator called/tested for theories
 *
 * Tests theory on comparable instances.
 */
@SuppressWarnings(value = {"unchecked", "rawtypes"})
@RunWith(Theories.class)
public abstract class CompareToTheory {

  private static void assumeThatValueIsNotNull(final Object uut) {
    assumeThat(uut, is(not(equalTo(null))));
  }

  private static String thrownMessage(
      final String prefix, final TestAtom atom1, final TestAtom atom2) {
    final StringBuilder buffer = new StringBuilder(64);
    buffer.append(prefix).append(" did not throw an exception, while ");
    appendMessage(buffer, atom1.label, atom1.e);
    buffer.append(" and");
    appendMessage(buffer, atom2.label, atom2.e);
    return buffer.toString();
  }

  private static void appendMessage(final StringBuilder buffer,
      final String label, final Exception thrownException) {
    buffer.append(label).append(" did ")
        .append(thrownException == null ? "not " : "");
  }

  private static boolean noExceptionThrown(final TestAtom... atoms) {
    for (final TestAtom e : atoms) {
      if (e.isExceptionThrown()) {
        return false;
      }
    }
    return true;
  }

  /**
   * Executes the test and stores the value, be it an exception or the
   * calculated value.
   */
  private static final class TestAtom {
    /**
     * A label for reporting failures.
     */
    private final String label;

    /**
     * The result comparing the UUTs.
     */
    private int value;

    /**
     * The exception raised (if any) during the comparing of the UUTs.
     */
    private Exception e;

    private TestAtom(final String label, final Comparable uutX, final Comparable uutY) {
      this.label = label;
      try {
        this.value = uutX.compareTo(uutY);
      } catch (final Exception e) {
        this.e = e;
      }
    }

    private boolean isExceptionThrown() {
      return e != null;
    }
  }

  /**
   * Checks the symmetric property of the {@link Comparable#compareTo(Object)}
   * method.
   * <p>
   * <blockquote>
   * <p>
   * The implementor must ensure <tt>sgn(x.compareTo(y)) ==
   * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>. (This implies
   * that <tt>x.compareTo(y)</tt> must throw an exception iff
   * <tt>y.compareTo(x)</tt> throws an exception.)
   * </p>
   * </blockquote>
   *
   * @param uutX the unit under test to test for symmetry.
   * @param uutY the unit under test to test for symmetry.
   * @see Comparable#compareTo(Object)
   */
  @Theory
  public void compareToIsSymmetric(final Comparable uutX, final Comparable uutY) {
    assumeThatValueIsNotNull(uutX);
    assumeThatValueIsNotNull(uutY);

    final TestAtom xToY = new TestAtom("X", uutX, uutY);
    final TestAtom yToX = new TestAtom("Y", uutY, uutX);

    if (noExceptionThrown(xToY, yToX)) {
      assertThat(xToY.value == -yToX.value, is(true));
    } else if (xToY.e != null) {
      assertThat("Only X threw an exception.", yToX.e, is(not(equalTo(null))));
    } else {
      Assert.fail("Only Y threw an exception.");
    }
  }

  /**
   * Checks the transitive property of the {@link Comparable#compareTo(Object)}
   * method.
   * <p>
   * <blockquote>
   * <p>
   * The implementor must also ensure that the relation is transitive:
   * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
   * <tt>x.compareTo(z)&gt;0</tt>.
   * </p>
   * <p>
   * <p>
   * Finally, the implementor must ensure that <tt>x.compareTo(y)==0</tt>
   * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for all
   * <tt>z</tt>.
   * </p>
   * </blockquote>
   *
   * @param uutX the unit under test to test for transitivity.
   * @param uutY the unit under test to test for transitivity.
   * @param uutZ the unit under test to test for transitivity.
   * @see Comparable#compareTo(Object)
   */
  @Theory
  public final void compareToIsTransitive(
      final Comparable uutX, final Comparable uutY, final Comparable uutZ) {
    assumeThatValueIsNotNull(uutX);
    assumeThatValueIsNotNull(uutY);

    final TestAtom xToY = new TestAtom("X", uutX, uutY);
    final TestAtom yToZ = new TestAtom("Y", uutY, uutZ);
    final TestAtom xToZ = new TestAtom("Z", uutX, uutZ);

    if (noExceptionThrown(xToY, yToZ, xToZ)) {
      assumeThat(xToY.value > 0 && yToZ.value > 0, is(equalTo(true)));
      assertThat(xToZ.value > 0, is(equalTo(true)));
    } else {
      assertThat(thrownMessage("X", yToZ, xToZ), xToY, is(not(equalTo(null))));
      assertThat(thrownMessage("Y", xToY, xToZ), yToZ, is(not(equalTo(null))));
      assertThat(thrownMessage("Z", xToY, yToZ), xToZ, is(not(equalTo(null))));
    }
  }

  /**
   * Checks the optional consistent to equals property of the
   * {@link Comparable#compareTo(Object)} method.
   * <p>
   * <blockquote>
   * <p>
   * It is strongly recommended, but <i>not</i> strictly required that
   * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>. Generally speaking, any
   * class that implements the <tt>Comparable</tt> interface and violates this
   * condition should clearly indicate this fact. The recommended language is
   * "Note: this class has a natural ordering that is inconsistent with equals."
   * </p>
   * </blockquote>
   *
   * @param uutX the unit under test to test for consistency with equals.
   * @param uutY the unit under test to test for consistency with equals.
   * @see Comparable#compareTo(Object)
   */
  @Theory
  public final void compareToIsConsistentToEquals(final Comparable uutX, final Comparable uutY) {
    assumeThatValueIsNotNull(uutX);

    final TestAtom xToY = new TestAtom("X", uutX, uutY);

    assumeThat(xToY.isExceptionThrown(), is(equalTo(false)));
    assumeThat(xToY.value, is(equalTo(0)));

    assertThat(uutX.equals(uutY), is(equalTo(true)));
  }
}