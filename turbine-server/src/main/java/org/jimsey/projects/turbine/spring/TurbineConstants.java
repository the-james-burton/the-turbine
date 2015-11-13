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
package org.jimsey.projects.turbine.spring;

public class TurbineConstants {

  public static final long PRODUCER_PERIOD = 2000;

  public static final String REST_ROOT_TICKS = "/tick";

  public static final String REST_ROOT_INDICATORS = "/indicator";

  public static final String REST_ROOT_STRATEGIES = "/strategy";

  public static final String REST_ROOT_TEST = "/test";

  public static final String HEADER_FOR_OBJECT_TYPE = "objectType";

  public static final String ELASTICSEARCH_INDEX_FOR_TICKS = "turbine-ticks";

  public static final String ELASTICSEARCH_INDEX_FOR_INDICATORS = "turbine-indicators";

  public static final String ELASTICSEARCH_INDEX_FOR_STRATEGIES = "turbine-strategies";

  public static final String ELASTICSEARCH_TYPE_FOR_TICKS = "turbine-tick";

  public static final String ELASTICSEARCH_TYPE_FOR_INDICATORS = "turbine-indicator";

  public static final String ELASTICSEARCH_TYPE_FOR_STRATEGIES = "turbine-strategy";

}
