/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.supplemental.fileupload;


/**
 * Instances of this interface are used to react to state changes of an ongoing file upload.
 */
public interface IFileUploadListener {

  /**
   * Called when new information about an in-progress upload is available.
   * 
   * @param event event object that contains information about the uploaded file
   * @see FileUploadEvent#getBytesRead()
   */
  void uploadProgress( FileUploadEvent event );

  /**
   * Called when a file upload has finished successfully.
   * 
   * @param event event object that contains information about the uploaded file
   * @see FileUploadEvent
   */
  void uploadFinished( FileUploadEvent event );

  /**
   * Called when a file upload failed.
   * 
   * @param event event object that contains information about the uploaded file
   * @see FileUploadEvent#getErrorMessage()
   */
  void uploadFailed( FileUploadEvent event );
}
