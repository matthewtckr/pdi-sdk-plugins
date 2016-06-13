/*! ******************************************************************************
*
* Pentaho Data Integration
*
* Copyright (C) 2002-2016 by Pentaho : http://www.pentaho.com
*
*******************************************************************************
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with
* the License. You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
******************************************************************************/

package org.pentaho.di.sdk.samples.imp.rules.demo;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.annotations.ImportRulePlugin;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.imp.rule.ImportValidationFeedback;
import org.pentaho.di.imp.rule.ImportValidationResultType;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entries.copyfiles.JobEntryCopyFiles;
import org.pentaho.di.job.entries.eval.JobEntryEval;
import org.pentaho.di.job.entries.http.JobEntryHTTP;
import org.pentaho.di.job.entries.mail.JobEntryMail;
import org.pentaho.di.job.entries.shell.JobEntryShell;
import org.pentaho.di.job.entries.special.JobEntrySpecial;
import org.pentaho.di.job.entries.sql.JobEntrySQL;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.calculator.CalculatorMeta;
import org.pentaho.di.trans.steps.janino.JaninoMeta;
import org.pentaho.di.trans.steps.javafilter.JavaFilterMeta;
import org.pentaho.di.trans.steps.rowgenerator.RowGeneratorMeta;
import org.pentaho.di.trans.steps.script.ScriptMeta;
import org.pentaho.di.trans.steps.scriptvalues_mod.ScriptValuesMetaMod;
import org.pentaho.di.trans.steps.ssh.SSHMeta;
import org.pentaho.di.trans.steps.tableinput.TableInputMeta;
import org.pentaho.di.trans.steps.userdefinedjavaclass.UserDefinedJavaClassMeta;

public class NoScriptsImportRuleTest {

  @BeforeClass
  public static void setUpBeforeClass() throws KettleException {
    KettleEnvironment.init( false );
  }

  @Test
  public void testDisabledByDefault() {
    NoScriptsImportRule rule = new NoScriptsImportRule();
    assertFalse( rule.isEnabled() );
  }

  @Test
  public void testIsAnnotated() {
    ImportRulePlugin annotation = NoScriptsImportRule.class.getAnnotation( ImportRulePlugin.class );
    assertNotNull( annotation.id() );
    assertNotNull( annotation.name() );
    assertNotNull( annotation.description() );
    assertNotNull( annotation.i18nPackageName() );
  }

  @Test
  public void testTrans() {
    testTransWithStep( new StepMeta( "Calculator", new CalculatorMeta() ), false );
    testTransWithStep( new StepMeta( "TableInput", new TableInputMeta() ), false );

    // Script-style steps
    testTransWithStep( new StepMeta( "UDJC", new UserDefinedJavaClassMeta() ), true );
    testTransWithStep( new StepMeta( "UDJE", new JaninoMeta() ), true );
    testTransWithStep( new StepMeta( "Java Filter", new JavaFilterMeta() ), true );
    testTransWithStep( new StepMeta( "Script", new ScriptMeta() ), true );
    testTransWithStep( new StepMeta( "JavaScript", new ScriptValuesMetaMod() ), true );
    testTransWithStep( new StepMeta( "SSH", mock( SSHMeta.class ) ), true );
  }

  @Test
  public void testJob() {
    testJobWithJobEntry( new JobEntryCopy( new JobEntryMail() ), false );
    testJobWithJobEntry( new JobEntryCopy( new JobEntrySQL() ), false );
    testJobWithJobEntry( new JobEntryCopy( new JobEntryHTTP() ), false );
    testJobWithJobEntry( new JobEntryCopy( new JobEntryCopyFiles() ), false );

    // Script-style job entries
    testJobWithJobEntry( new JobEntryCopy( new JobEntryShell() ), true );
    testJobWithJobEntry( new JobEntryCopy( new JobEntryEval() ), true );
  }

  private void testTransWithStep( StepMeta stepMeta, boolean isScriptTypeStep ) {
    TransMeta transMeta = new TransMeta();
    transMeta.addStep( new StepMeta( "Generate Rows", new RowGeneratorMeta() ) );
    transMeta.addStep( stepMeta );

    NoScriptsImportRule rule = new NoScriptsImportRule();
    rule.setEnabled( true );
    List<ImportValidationFeedback> results = rule.verifyRule( transMeta );

    checkFeedbackHasError( results, isScriptTypeStep );
  }

  private void testJobWithJobEntry( JobEntryCopy jobEntry, boolean isScriptTypeEntry ) {
    JobMeta jobMeta = new JobMeta();
    jobMeta.addJobEntry( new JobEntryCopy( new JobEntrySpecial( "Start", true, false ) ) );
    jobMeta.addJobEntry( new JobEntryCopy( new JobEntrySpecial( "Dummy", false, true ) ) );
    jobMeta.addJobEntry( jobEntry );

    NoScriptsImportRule rule = new NoScriptsImportRule();
    rule.setEnabled( true );
    List<ImportValidationFeedback> results = rule.verifyRule( jobMeta );

    checkFeedbackHasError( results, isScriptTypeEntry );
  }

  private static void checkFeedbackHasError( List<ImportValidationFeedback> feedback, boolean shouldHaveError ) {
    assertNotNull( feedback );
    assertFalse( feedback.isEmpty() );
    boolean hasErrorFeedback = false;
    for ( ImportValidationFeedback entry : feedback ) {
      if ( entry.getResultType() == ImportValidationResultType.ERROR ) {
        hasErrorFeedback = true;
      }
    }
    assertTrue( shouldHaveError == hasErrorFeedback );
  }
}
