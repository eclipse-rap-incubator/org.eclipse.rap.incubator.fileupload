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
package org.eclipse.rap.addons.filedialog.demo.examples;

import java.io.File;

import org.eclipse.rap.examples.ExampleUtil;
import org.eclipse.rap.examples.IExamplePage;
import org.eclipse.rap.rwt.service.ServerPushSession;
import org.eclipse.rap.rwt.supplemental.fileupload.DiskFileUploadReceiver;
import org.eclipse.rap.rwt.supplemental.fileupload.FileUploadEvent;
import org.eclipse.rap.rwt.supplemental.fileupload.FileUploadHandler;
import org.eclipse.rap.rwt.supplemental.fileupload.FileUploadListener;
import org.eclipse.rap.rwt.widgets.DialogCallback;
import org.eclipse.rap.rwt.widgets.DialogUtil;
import org.eclipse.rap.rwt.widgets.FileUpload;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class FileUploadExamplePage implements IExamplePage {

  private static final String INITIAL_TEXT = "no files uploaded.";
  private FileUpload fileUpload;
  private Label fileNameLabel;
  private Button uploadButton;
  private Text logText;
  private ServerPushSession pushSession;

  public void createControl( Composite parent ) {
    parent.setLayout( ExampleUtil.createMainLayout( 3 ) );
    Control controlsColumn = createControlsColumn( parent );
    controlsColumn.setLayoutData( ExampleUtil.createFillData() );
    Control serverColumn = createLogColumn( parent );
    serverColumn.setLayoutData( ExampleUtil.createFillData() );
    Control infoColumn = createInfoColumn( parent );
    infoColumn.setLayoutData( ExampleUtil.createFillData() );
  }

  private Control createControlsColumn( Composite parent ) {
    Composite column = new Composite( parent, SWT.NONE );
    column.setLayout( ExampleUtil.createGridLayoutWithoutMargin( 1, false ) );
    Control fileUploadArea = createFileUploadArea( column );
    fileUploadArea.setLayoutData( ExampleUtil.createHorzFillData() );
    Control fileDialogArea = createFileDialogArea( column );
    fileDialogArea.setLayoutData( ExampleUtil.createHorzFillData() );
    return column;
  }

  private Control createLogColumn( Composite parent ) {
    Composite column = new Composite( parent, SWT.NONE );
    column.setLayout( ExampleUtil.createGridLayout( 1, false, true, true ) );
    ExampleUtil.createHeading( column, "Server log", 2 );
    logText = new Text( column, SWT.MULTI | SWT.READ_ONLY | SWT.H_SCROLL | SWT.BORDER );
    logText.setText( INITIAL_TEXT );
    logText.setLayoutData( ExampleUtil.createFillData() );
    createClearButton( column );
    return column;
  }

  private static Control createInfoColumn( Composite parent ) {
    Label label = new Label( parent, SWT.NONE );
    label.setLayoutData( ExampleUtil.createFillData() );
    return label;
  }

  private Control createFileUploadArea( Composite parent ) {
    Composite area = new Composite( parent, SWT.NONE );
    area.setLayout( ExampleUtil.createGridLayout( 2, true, true, true ) );
    ExampleUtil.createHeading( area, "FileUpload widget", 2 );
    fileUpload = new FileUpload( area, SWT.NONE );
    fileUpload.setText( "Select File" );
    fileUpload.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, false, false ) );
    fileNameLabel = new Label( area, SWT.NONE );
    fileNameLabel.setText( "no file selected" );
    fileNameLabel.setLayoutData( ExampleUtil.createHorzFillData() );
    fileNameLabel.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
    uploadButton = new Button( area, SWT.PUSH );
    uploadButton.setText( "Upload" );
    uploadButton.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, false, false ) );
    new Label( area, SWT.NONE );
    fileUpload.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        String fileName = fileUpload.getFileName();
        fileNameLabel.setText( fileName == null ? "" : fileName );
      }
    } );
    final String url = startUploadReceiver();
    pushSession = new ServerPushSession();
    uploadButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        pushSession.start();
        fileUpload.submit( url );
      }
    } );
    return area;
  }

  private String startUploadReceiver() {
    DiskFileUploadReceiver receiver = new DiskFileUploadReceiver();
    FileUploadHandler uploadHandler = new FileUploadHandler( receiver );
    uploadHandler.addUploadListener( new FileUploadListener() {

      public void uploadProgress( FileUploadEvent event ) {
        // handle upload progress
      }

      public void uploadFailed( FileUploadEvent event ) {
        addToLog( "upload failed: " + event.getFileName() );
      }

      public void uploadFinished( FileUploadEvent event ) {
        addToLog( "received: " + event.getFileName() );
      }
    } );
    return uploadHandler.getUploadUrl();
  }

  private void addToLog( final String message ) {
    if( !logText.isDisposed() ) {
      logText.getDisplay().asyncExec( new Runnable() {
        public void run() {
          String text = logText.getText();
          if( INITIAL_TEXT.equals( text ) ) {
            text = "";
          }
          logText.setText( text + message + "\n" );
          pushSession.stop();
        }
      } );
    }
  }

  private Composite createFileDialogArea( Composite parent ) {
    Composite area = new Composite( parent, SWT.NONE );
    area.setLayout( ExampleUtil.createGridLayout( 2, true, true, true ) );
    ExampleUtil.createHeading( area, "FileDialog", 2 );
    createAddSingleButton( area );
    new Label( area, SWT.NONE );
    createAddMultiButton( area );
    return area;
  }

  private void createAddSingleButton( Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setLayoutData( ExampleUtil.createHorzFillData() );
    button.setText( "Single File" );
    button.setToolTipText( "Launches file dialog for single file selection." );
    final Shell parentShell = parent.getShell();
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        openFileDialog( parentShell, false );
      }
    } );
  }

  private void createAddMultiButton( Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setLayoutData( ExampleUtil.createHorzFillData() );
    button.setText( "Multiple Files" );
    button.setToolTipText( "Launches file dialog for multiple file selection." );
    final Shell parentShell = parent.getShell();
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        openFileDialog( parentShell, true );
      }
    } );
  }

  private void createClearButton( Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setLayoutData( ExampleUtil.createHorzFillData() );
    button.setText( "Clear" );
    button.setToolTipText( "Clears the results list" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        logText.setText( INITIAL_TEXT );
      }
    } );
  }

  private void openFileDialog( Shell parent, boolean multi ) {
    final FileDialog fileDialog = new FileDialog( parent, getDialogStyle( multi ) );
    fileDialog.setText( multi ? "Upload Multiple Files" : "Upload Single File" );
    DialogUtil.open( fileDialog, new DialogCallback() {
      public void dialogClosed( int returnCode ) {
        showUploadResults( fileDialog );
      }
    } );
  }

  private void showUploadResults( FileDialog fileDialog ) {
    String[] selectedFiles = fileDialog.getFileNames();
    for( String fileName : selectedFiles ) {
      addToLog( "received: " + new File( fileName ).getName() );
    }
  }

  private static int getDialogStyle( boolean multi ) {
    int result = SWT.SHELL_TRIM | SWT.APPLICATION_MODAL;
    if( multi ) {
      result |= SWT.MULTI;
    }
    return result;
  }

}
