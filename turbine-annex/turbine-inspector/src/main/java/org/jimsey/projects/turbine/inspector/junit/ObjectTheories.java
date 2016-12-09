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
package org.jimsey.projects.turbine.inspector.junit;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

import java.lang.invoke.MethodHandles;

import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ORIGINAL VERSION : Copyright 2008-2013 smartics, Kronseder & Reiner GmbH
 * https://www.smartics.eu/confluence/display/BLOG/2013/02/26/Testing+equals,+hashCode,+et+al.+in+Java
 * 
 * Only reproduced here due to unavailability in maven central repo
 * 
 * Tests common object theories. This includes
 * <ol>
 * <li>{@link Object#equals(Object)}</li>
 * <li>{@link Object#hashCode()}</li>
 * <li>{@link Object#toString()}</li>
 * </ol>
 *
 */
@RunWith(Theories.class)
public abstract class ObjectTheories 
{

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  /**
   * The default value for the number of iterations for the
   * {@link #equalsIsConsistent(Object, Object) consistency check}. May be
   * specified by subclasses by overriding
   * {@link #getConsistencyIterationCount()}.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final int DEFAULT_COUNT_CONSISTENCY_CHECK = 7;

  /**
   * The default value to be returned by
   * {@link #checkForDifferentTypesInEquals()}.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final boolean DEFAULT_RUN_EQUALS_THEORY_ON_DIFFERENT_TYPES = true;

  /**
   * The default value to be returned by {@link #checkForUnequalHashCodes()}.
   * <p>
   * The value of this constant is {@value}.
   * </p>
   */
  public static final boolean DEFAULT_RUN_HASH_CODE_THEORY_ON_UNEQUAL_INSTANCES = true;

  /**
   * Type to instantiate for tests.
   */
  private static final class OtherType {
  }

  private static void assumeThatValueIsNotNull(final Object uut) {
    assumeThat(uut, is(not(equalTo(null))));
  }

  /**
   * Determines the default number of iterations to go through for the
   * consistency checks.
   *
   * @return {@value #DEFAULT_COUNT_CONSISTENCY_CHECK} per default.
   * @see #getEqualsConsistencyIterationCount()
   * @see #getHashCodeConsistencyIterationCount()
   */
  protected int getConsistencyIterationCount() {
    return DEFAULT_COUNT_CONSISTENCY_CHECK;
  }

  /**
   * Determines the number of iterations to go through for the
   * {@link #equalsIsConsistent(Object, Object) equals consistency check}.
   *
   * @return result of {@link #getConsistencyIterationCount()} per default.
   */
  protected int getEqualsConsistencyIterationCount() {
    return getConsistencyIterationCount();
  }

  /**
   * Determines the number of iterations to go through for the
   * {@link #hashCodeIsConsistent(Object) hash code consistency check}.
   *
   * @return result of {@link #getConsistencyIterationCount()} per default.
   */
  protected int getHashCodeConsistencyIterationCount() {
    return getConsistencyIterationCount();
  }

  /**
   * Determines if a theory to check that equals fails on different types should
   * be checked.
   *
   * @return <code>true</code> if the theory should be executed,
   *         <code>false</code> otherwise. Per default returns
   *         {@value #DEFAULT_RUN_EQUALS_THEORY_ON_DIFFERENT_TYPES}
   */
  protected boolean checkForDifferentTypesInEquals() {
    return DEFAULT_RUN_EQUALS_THEORY_ON_DIFFERENT_TYPES;
  }

  /**
   * Determines if a theory to check for unequal hash code values for unequal
   * instances should be checked.
   *
   * @return <code>true</code> if the theory should be executed,
   *         <code>false</code> otherwise. Per default returns
   *         {@value #DEFAULT_RUN_HASH_CODE_THEORY_ON_UNEQUAL_INSTANCES}
   */
  protected boolean checkForUnequalHashCodes() {
    return DEFAULT_RUN_HASH_CODE_THEORY_ON_UNEQUAL_INSTANCES;
  }

  /**
   * Checks the reflexive property of the {@link Object#equals(Object)} method.
   * <p>
   * <blockquote>
   * <p>
   * It is <i>reflexive</i>: for any non-null reference value <code>x</code>,
   * <code>x.equals(x)</code> should return <code>true</code>.
   * </p>
   * </blockquote>
   *
   * @param uut the unit under test.
   * @see Object#equals(Object)
   */
  @Theory
  public final void equalsIsReflexive(final Object uut) {
    logger.debug("equalsIsReflexive: [{}]", uut);
    assumeThatValueIsNotNull(uut);
    assertThat(uut.equals(uut), is(true));
  }

  /**
   * Checks the symmetric property of the {@link Object#equals(Object)} method.
   * <p>
   * <blockquote>
   * <p>
   * It is <i>symmetric</i>: for any non-null reference values <code>x</code>
   * and <code>y</code>, <code>x.equals(y)</code> should return
   * <code>true</code> if and only if <code>y.equals(x)</code> returns
   * <code>true</code>.
   * </p>
   * </blockquote>
   *
   * @param uutX the unit under test to test for symmetry.
   * @param uutY the unit under test to test for symmetry.
   * @see Object#equals(Object)
   */
  @Theory
  public final void equalsIsSymmetric(final Object uutX, final Object uutY) {
    logger.debug("equalsIsSymmetric: [{}, {}]", uutX, uutY);
    assumeThatValueIsNotNull(uutX);
    assumeThatValueIsNotNull(uutY);

    assumeThat(uutY.equals(uutX), is(true));
    assertThat(uutX.equals(uutY), is(true));
  }

  /**
   * Checks the transitive property of the {@link Object#equals(Object)} method.
   * <p>
   * <blockquote>
   * <p>
   * It is <i>transitive</i>: for any non-null reference values <code>x</code>,
   * <code>y</code>, and <code>z</code>, if <code>x.equals(y)</code> returns
   * <code>true</code> and <code>y.equals(z)</code> returns <code>true</code>,
   * then <code>x.equals(z)</code> should return <code>true</code>.
   * </p>
   * </blockquote>
   *
   * @param uutX the unit under test to test for transitivity.
   * @param uutY the unit under test to test for transitivity.
   * @param uutZ the unit under test to test for transitivity.
   * @see Object#equals(Object)
   */
  @Theory
  public final void equalsIsTransitive(final Object uutX, final Object uutY,
      final Object uutZ) {
    logger.debug("equalsIsTransitive: [{}, {}, {}]", uutX, uutY, uutZ);
    assumeThatValueIsNotNull(uutX);
    assumeThatValueIsNotNull(uutY);
    assumeThatValueIsNotNull(uutZ);
    assumeThat(uutX.equals(uutY) && uutY.equals(uutZ), is(true));
    assertThat(uutZ.equals(uutX), is(true));
  }

  /**
   * Checks the consistent property of the {@link Object#equals(Object)} method.
   * <p>
   * <blockquote>
   * <p>
   * It is <i>consistent</i>: for any non-null reference values <code>x</code>
   * and <code>y</code>, multiple invocations of <tt>x.equals(y)</tt>
   * consistently return <code>true</code> or consistently return
   * <code>false</code>, provided no information used in <code>equals</code>
   * comparisons on the objects is modified.
   * </p>
   * </blockquote>
   *
   * @param uutX the unit under test to test for consistency.
   * @param uutY the unit under test to test for consistency.
   * @see Object#equals(Object)
   */
  @Theory
  public final void equalsIsConsistent(final Object uutX, final Object uutY) {
    logger.debug("equalsIsConsistent: [{}, {}]", uutX, uutY);
    assumeThatValueIsNotNull(uutX);

    final boolean result = uutX.equals(uutY);
    for (int i = getEqualsConsistencyIterationCount(); i > 0; i--) {
      assertThat(uutX.equals(uutY), is(result));
    }
  }

  /**
   * Checks the not-null property of the {@link Object#equals(Object)} method.
   * <p>
   * <blockquote>
   * <p>
   * For any non-null reference value <code>x</code>,
   * <code>x.equals(null)</code> should return <code>false</code>.
   * </p>
   * </blockquote>
   *
   * @param uut the unit under test.
   * @see Object#equals(Object)
   */
  @Theory
  public final void equalsReturnFalseOnNull(final Object uut) {
    logger.debug("equalsReturnFalseOnNull: [{}]", uut);
    assumeThatValueIsNotNull(uut);

    assertThat(uut.equals(null), is(false)); // NOPMD
  }

  /**
   * Checks the consistent property of the {@link Object#equals(Object)} method
   * regarding comparison with elements of other type.
   *
   * @param uut the unit under test.
   * @see Object#equals(Object)
   */
  @Theory
  public final void equalsReturnFalseOnInstanceOfOtherType(final Object uut) {
    if (checkForDifferentTypesInEquals()) {
      logger.debug("equalsReturnFalseOnInstanceOfOtherType: [{}]", uut);
      assumeThatValueIsNotNull(uut);

      final Object instanceOfOtherType = new OtherType();
      assertThat(uut.equals(instanceOfOtherType), is(false)); // NOPMD
    }
  }

  // ... hashCode .............................................................

  /**
   * Checks the consistency property of the {@link Object#hashCode()} method.
   * <p>
   * <blockquote>
   * <p>
   * Whenever it is invoked on the same object more than once during an
   * execution of a Java application, the <tt>hashCode</tt> method must
   * consistently return the same integer, provided no information used in
   * <tt>equals</tt> comparisons on the object is modified. This integer need
   * not remain consistent from one execution of an application to another
   * execution of the same application.
   * </p>
   * </blockquote>
   *
   * @param uut the unit under test.
   * @see Object#hashCode()
   */
  @Theory
  public final void hashCodeIsConsistent(final Object uut) {
    logger.debug("hashCodeIsConsistent: [{}]", uut);
    assumeThatValueIsNotNull(uut);

    final int hashCode = uut.hashCode();
    for (int i = getHashCodeConsistencyIterationCount(); i > 0; i--) {
      assertThat(uut.hashCode(), is(hashCode));
    }
  }

  /**
   * Checks the consistency-with-equals property of the
   * {@link Object#hashCode()} method.
   * <p>
   * <blockquote>
   * <p>
   * If two objects are equal according to the <tt>equals(Object)</tt> method,
   * then calling the <code>hashCode</code> method on each of the two objects
   * must produce the same integer result.
   * </p>
   * </blockquote>
   *
   * @param uutX the unit under test to test for consistency-with-equals.
   * @param uutY the unit under test to test for consistency-with-equals.
   * @see Object#hashCode()
   */
  @Theory
  public final void hashCodeIsConsistentWithEquals(final Object uutX,
      final Object uutY) {
    logger.debug("hashCodeIsConsistentWithEquals: [{}, {}]", uutX, uutY);
    assumeThatValueIsNotNull(uutX);
    assumeThat(uutX.equals(uutY), is(true)); // implicitly uutY is not 'null'.

    assertThat(uutX.hashCode(), is(equalTo(uutY.hashCode())));
  }

  /**
   * Checks the guideline for optimum performance of the
   * {@link Object#hashCode()} method.
   * <p>
   * <blockquote>
   * <p>
   * It is <em>not</em> required that if two objects are unequal according to
   * the {@link java.lang.Object#equals(java.lang.Object)} method, then calling
   * the <tt>hashCode</tt> method on each of the two objects must produce
   * distinct integer results. However, the programmer should be aware that
   * producing distinct integer results for unequal objects may improve the
   * performance of hashtables.
   * </p>
   * </blockquote>
   *
   * @param uutX the unit under test.
   * @param uutY the unit under test.
   * @see Object#hashCode()
   */
  @Theory
  public final void hashCodeProducesUnequalHashCodesForUnequalInstances(
      final Object uutX, final Object uutY) {
    if (checkForUnequalHashCodes()) {
      logger.debug("hashCodeProducesUnequalHashCodesForUnequalInstances: [{}, {}]", uutX, uutY);
      assumeThatValueIsNotNull(uutX);
      assumeThatValueIsNotNull(uutY);
      assumeThat(uutX.equals(uutY), is(false));
      assertThat(uutX.hashCode(), is(not(equalTo(uutY.hashCode()))));
    }
  }

  // ... toString .............................................................

  /**
   * This theory simply checks that calling the {@link Object#toString()} method
   * of the unit under test (UUT) does not fail with raising an exception.
   * <p>
   * The theory is only executed on UUTs that are not <code>null</code>.
   * </p>
   *
   * @param uut the unit under test.
   */
  @Theory
  public final void toStringRunsWithoutFailure(final Object uut) {
    logger.debug("toStringRunsWithoutFailure: [{}]", uut);
    assumeThatValueIsNotNull(uut);
    final String string = uut.toString();
    assertThat(string, is(not(equalTo(null))));
  }
}
