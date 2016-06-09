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

package org.pentaho.di.sdk.samples.encryption.reverse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.di.core.encryption.TwoWayPasswordEncoderPlugin;
import org.pentaho.di.core.exception.KettleException;

public class DemoTwoWayPasswordEncoderTest {

  DemoTwoWayPasswordEncoder encoder;

  @Before
  public void setUp() throws KettleException {
    encoder = new DemoTwoWayPasswordEncoder();
    encoder.init();
  }

  @Test
  public void testGetPrefixes() {
    assertNotNull( encoder.getPrefixes() );
    assertEquals( 1, encoder.getPrefixes().length );
    assertEquals( "Reversed ", encoder.getPrefixes()[0] );
  }

  @Test
  public void testEmptyInput() {
    assertEquals( "Reversed ", encoder.encode( null ) );
    assertEquals( "Reversed ", encoder.encode( null, false ) );
    assertEquals( "Reversed ", encoder.encode( null, true ) );
    assertEquals( "Reversed ", encoder.encode( "" ) );
    assertEquals( "Reversed ", encoder.encode( "", false ) );
    assertEquals( "Reversed ", encoder.encode( "", true ) );
    assertEquals( "", encoder.decode( null ) );
    assertEquals( "", encoder.decode( null, false ) );
    assertEquals( "", encoder.decode( null, true ) );
    assertEquals( "", encoder.decode( "" ) );
    assertEquals( "", encoder.decode( "", false ) );
    assertEquals( "", encoder.decode( "", true ) );
  }

  @Test
  public void testNonEmptyInput() {
    assertEquals( "Reversed a", encoder.encode( "a" ) );
    assertEquals( "Reversed a", encoder.encode( "a", false ) );
    assertEquals( "Reversed a", encoder.encode( "a", true ) );
    assertEquals( "Reversed ba", encoder.encode( "ab" ) );
    assertEquals( "Reversed ba", encoder.encode( "ab", false ) );
    assertEquals( "Reversed ba", encoder.encode( "ab", true ) );
    assertEquals( "Reversed desreveR", encoder.encode( "Reversed", true ) );
    assertEquals( "Reversed }raVtseT{$", encoder.encode( "${TestVar}", true ) );
    assertEquals( " desreveR", encoder.decode( "Reversed " ) );
    assertEquals( "", encoder.decode( "Reversed ", true ) );
    assertEquals( "", encoder.decode( "Reversed ", false ) );
    assertEquals( "a desreveR", encoder.decode( "Reversed a" ) );
    assertEquals( "a", encoder.decode( "Reversed a", true ) );
    assertEquals( "a", encoder.decode( "Reversed a", false ) );
    assertEquals( "ba", encoder.decode( "Reversed ab", true ) );
    assertEquals( "ba", encoder.decode( "Reversed ab", false ) );
    assertEquals( "Reversed", encoder.decode( "Reversed desreveR", true ) );
    assertEquals( "Reversed", encoder.decode( "Reversed desreveR", false ) );
    assertEquals( "${TestVar}", encoder.decode( "Reversed }raVtseT{$", true ) );
    assertEquals( "${TestVar}", encoder.decode( "Reversed }raVtseT{$", false ) );
  }

  @Test
  public void testAnnotations() {
    TwoWayPasswordEncoderPlugin annotation = encoder.getClass().getAnnotation( TwoWayPasswordEncoderPlugin.class );
    assertNotNull( annotation );
    assertNotNull( annotation.id() );
    assertNotNull( annotation.name() );
  }
}
