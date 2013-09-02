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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import org.eclipse.rap.rwt.supplemental.fileupload.internal.FileUploadDetails;
import org.eclipse.rap.rwt.supplemental.fileupload.test.FileUploadTestUtil;
import org.junit.After;
import org.junit.Test;


public class DiskFileUploadReceiver_Test {

  private File createdFile;
  private File createdContentTypeFile;

  @After
  public void tearDown() {
    if( createdFile != null ) {
      createdFile.delete();
      createdFile = null;
    }
    if ( createdContentTypeFile != null ) {
      createdContentTypeFile.delete();
      createdContentTypeFile = null;
    }
  }

  @Test
  public void testInitialGetTargetFile() {
    DiskFileUploadReceiver receiver = new DiskFileUploadReceiver();

    assertNull( receiver.getTargetFile() );
  }

  @Test
  public void testCreateTargetFile() throws IOException {
    DiskFileUploadReceiver receiver = new DiskFileUploadReceiver();

    IFileUploadDetails details = new FileUploadDetails( "foo.bar", "text/plain", 5 );
    createdFile = receiver.createTargetFile( details );

    assertTrue( createdFile.exists() );
    assertEquals( "foo.bar", createdFile.getName() );
  }

  @Test
  public void testCreateContentTypeFile() throws IOException {
    DiskFileUploadReceiver receiver = new DiskFileUploadReceiver();

    IFileUploadDetails details = new FileUploadDetails( "foo.bar", "text/plain", 5 );
    createdFile = receiver.createTargetFile( details );
    createdContentTypeFile = receiver.createContentTypeFile( createdFile, details );

    assertTrue( createdContentTypeFile.exists() );
  }

  @Test
  public void testGetContentType() throws IOException {
    testReceive();
    assertEquals( "text/plain", DiskFileUploadReceiver.getContentType( createdFile ) );
    assertEquals( null, DiskFileUploadReceiver.getContentType( new File( "test" ) ) );
  }

  @Test
  public void testCreatedTargetFilesDiffer() throws IOException {
    DiskFileUploadReceiver receiver = new DiskFileUploadReceiver();

    IFileUploadDetails details = new FileUploadDetails( "foo.bar", "text/plain", 5 );
    createdFile = receiver.createTargetFile( details );
    File createdFile2 = receiver.createTargetFile( details );
    createdFile2.deleteOnExit();

    assertFalse( createdFile.getAbsolutePath().equals( createdFile2.getAbsolutePath() ) );
  }

  @Test
  public void testReceive() throws IOException {
    DiskFileUploadReceiver receiver = new DiskFileUploadReceiver();
    String content = "Hello world!";

    IFileUploadDetails details = new FileUploadDetails( "foo.bar", "text/plain", content.length() );
    receiver.receive( new ByteArrayInputStream( content.getBytes() ), details );
    createdFile = receiver.getTargetFile();

    assertNotNull( createdFile );
    assertTrue( createdFile.exists() );
    assertEquals( content, FileUploadTestUtil.getFileContents( createdFile ) );
  }

  @Test
  public void testReceiveWithNullDetails() throws IOException {
    DiskFileUploadReceiver receiver = new DiskFileUploadReceiver();
    String content = "Hello world!";

    receiver.receive( new ByteArrayInputStream( content.getBytes() ), null );
    createdFile = receiver.getTargetFile();

    assertNotNull( createdFile );
    assertTrue( createdFile.exists() );
    assertEquals( "upload.tmp", createdFile.getName() );
    assertEquals( content, FileUploadTestUtil.getFileContents( createdFile ) );
  }

}
