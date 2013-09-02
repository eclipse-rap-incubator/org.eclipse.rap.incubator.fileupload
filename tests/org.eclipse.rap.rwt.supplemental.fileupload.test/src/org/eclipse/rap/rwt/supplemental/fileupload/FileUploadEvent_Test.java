/*******************************************************************************
 * Copyright (c) 2011, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.supplemental.fileupload;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.eclipse.rap.rwt.supplemental.fileupload.test.TestFileUploadEvent;
import org.eclipse.rap.rwt.supplemental.fileupload.test.TestFileUploadListener;
import org.eclipse.rap.rwt.supplemental.fileupload.test.TestFileUploadReceiver;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class FileUploadEvent_Test {

  private FileUploadHandler handler;

  @Before
  public void setUp() {
    Fixture.setUp();
    handler = new FileUploadHandler( new TestFileUploadReceiver() );
  }

  @After
  public void tearDown() {
    handler.dispose();
    handler = null;
    Fixture.tearDown();
  }

  @Test
  public void testCannotCreateWithNullSource() {
    try {
      new TestFileUploadEvent( null );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testGetSource() {
    TestFileUploadEvent event = new TestFileUploadEvent( handler );

    assertSame( handler, event.getSource() );
  }

  @Test
  public void testDispatchProgress() {
    TestFileUploadListener listener = new TestFileUploadListener();
    handler.addUploadListener( listener );

    new TestFileUploadEvent( handler ).dispatchProgress();

    assertEquals( "progress.", listener.getLog() );
  }

  @Test
  public void testDispatchFinished() {
    TestFileUploadListener listener = new TestFileUploadListener();
    handler.addUploadListener( listener );

    new TestFileUploadEvent( handler ).dispatchFinished();

    assertEquals( "finished.", listener.getLog() );
  }

  @Test
  public void testDispatchFailed() {
    TestFileUploadListener listener = new TestFileUploadListener();
    handler.addUploadListener( listener );

    new TestFileUploadEvent( handler ).dispatchFailed();

    assertEquals( "failed.", listener.getLog() );
  }

}
