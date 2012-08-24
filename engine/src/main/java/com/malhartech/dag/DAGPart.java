/*
 *  Copyright (c) 2012 Malhar, Inc.
 *  All Rights Reserved.
 */
package com.malhartech.dag;

import org.apache.hadoop.conf.Configuration;

/**
 *
 * The common base interface for runtime objects of streams and nodes<p>
 * <br>
 * common interface for all DAG run time objects<br>
 * <br>
 * @author Chetan Narsude <chetan@malhar-inc.com>
 */
public interface DAGPart<T1 extends Configuration, T2 extends Context>
{
  public void setup(T1 config);
  public void teardown();
}
