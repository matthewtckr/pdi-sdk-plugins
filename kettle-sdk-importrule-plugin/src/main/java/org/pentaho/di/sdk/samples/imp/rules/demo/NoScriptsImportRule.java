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

import java.util.ArrayList;
import java.util.List;

import org.pentaho.di.core.annotations.ImportRulePlugin;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.imp.rule.ImportValidationFeedback;
import org.pentaho.di.imp.rule.ImportValidationResultType;
import org.pentaho.di.imp.rules.BaseImportRule;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entries.eval.JobEntryEval;
import org.pentaho.di.job.entries.shell.JobEntryShell;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.pentaho.di.job.entry.JobEntryInterface;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.janino.JaninoMeta;
import org.pentaho.di.trans.steps.javafilter.JavaFilterMeta;
import org.pentaho.di.trans.steps.script.ScriptMeta;
import org.pentaho.di.trans.steps.scriptvalues_mod.ScriptValuesMetaMod;
import org.pentaho.di.trans.steps.ssh.SSHMeta;
import org.pentaho.di.trans.steps.userdefinedjavaclass.UserDefinedJavaClassMeta;

@ImportRulePlugin(
  id = "DemoImportRule",
  name = "DemoImportRule.Name",
  description = "DemoImportRule.Description",
  i18nPackageName = "org.pentaho.di.sdk.samples.imp.rules.demo" )
public class NoScriptsImportRule extends BaseImportRule {

  private static final Class<?> PKG = NoScriptsImportRule.class;

  @Override
  public List<ImportValidationFeedback> verifyRule( Object subject ) {
    List<ImportValidationFeedback> feedback = new ArrayList<ImportValidationFeedback>();

    /*
     * If this rule is not enabled, do nothing
     */
    if ( !isEnabled() || subject == null ) {
      return feedback;
    }

    if ( subject instanceof TransMeta ) {
      verifyTrans( (TransMeta) subject, feedback );
    } else if ( subject instanceof JobMeta ) {
      verifyJob( (JobMeta) subject, feedback );
    }
    return feedback;
  }

  /**
   * Review the JobMeta to determine if any Job Entries are of a specific type which allow for custom
   * Java or Shell scripting
   *
   * @param subject  The TransMeta object to be checked
   * @param feedback A list of Import Validation results
   */
  private void verifyTrans( TransMeta subject, List<ImportValidationFeedback> feedback ) {
    if ( subject == null ) {
      return;
    }
    boolean foundScript = false;
    for ( StepMeta stepMeta : subject.getSteps() ) {
      StepMetaInterface smi = stepMeta.getStepMetaInterface();
      if ( smi == null ) {
        continue;
      }
      if ( smi instanceof UserDefinedJavaClassMeta
          || smi instanceof JaninoMeta
          || smi instanceof JavaFilterMeta
          || smi instanceof ScriptMeta
          || smi instanceof ScriptValuesMetaMod
          || smi instanceof SSHMeta ) {
        foundScript = true;
        feedback.add( new ImportValidationFeedback( this, ImportValidationResultType.ERROR,
          BaseMessages.getString( PKG, "NoScriptsImportRule.TransContainsScript" ) ) );
        break;
      }
    }
    if ( !foundScript ) {
      feedback.add( new ImportValidationFeedback( this, ImportValidationResultType.APPROVAL,
        BaseMessages.getString( PKG, "NoScriptsImportRule.TransDoesNotHaveScript" ) ) );
    }
  }

  /**
   * Review the JobMeta to determine if any Job Entries are of a specific type which allow for custom
   * Java or Shell scripting
   *
   * @param subject  The JobMeta object to be checked
   * @param feedback A list of Import Validation results
   */
  private void verifyJob( JobMeta subject, List<ImportValidationFeedback> feedback ) {
    if ( subject == null ) {
      return;
    }
    boolean foundScript = false;
    for ( JobEntryCopy copy : subject.getJobCopies() ) {
      JobEntryInterface entry = copy.getEntry();
      if ( entry == null ) {
        continue;
      }
      if ( entry instanceof JobEntryShell
          || entry instanceof JobEntryEval ) {
        foundScript = true;
        feedback.add( new ImportValidationFeedback( this, ImportValidationResultType.ERROR,
          BaseMessages.getString( PKG, "NoScriptsImportRule.JobContainsScript" ) ) );
        break;
      }
    }
    if ( !foundScript ) {
      feedback.add( new ImportValidationFeedback( this, ImportValidationResultType.APPROVAL,
        BaseMessages.getString( PKG, "NoScriptsImportRule.JobDoesNotHaveScript" ) ) );
    }
  }
}
