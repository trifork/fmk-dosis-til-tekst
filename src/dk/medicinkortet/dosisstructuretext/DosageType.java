/**
* The contents of this file are subject to the Mozilla Public
* License Version 1.1 (the "License"); you may not use this file
* except in compliance with the License. You may obtain a copy of
* the License at http://www.mozilla.org/MPL/
*
* Software distributed under the License is distributed on an "AS
* IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
* implied. See the License for the specific language governing
* rights and limitations under the License.
*
* Contributor(s): Contributors are attributed in the source code
* where applicable.
*
* The Original Code is "Dosis-til-tekst".
*
* The Initial Developer of the Original Code is Trifork Public A/S.
*
* Portions created for the FMK Project are Copyright 2011,
* National Board of e-Health (NSI). All Rights Reserved.
*/

package dk.medicinkortet.dosisstructuretext;

public enum DosageType {

    AccordingToNeed, // ”efter behov”: En dosering der udelukkende gives efter behov. Doseringen kan evt. have en begrænsning på en maksimal dagsdosis.
    Temporary, // ”temporær”: En dosering med en slutdato eller en dosering der ikke er gentaget (elementet NotIterated er anvendt i stedet for IterationInterval). Desuden skal doseringen ikke helt eller delivist gives efter behov.
    Fixed, 	   // ”fast”: En itereret dosering uden slutdato, der ikke helt eller delvist gives efter behov.
    OneTime,   // "engangs”: En dosering med kun en enkelt dosis, der ikke gives efter behov.
    Combined, // ”kombineret”: En dosering der både gives (temporært eller fast) og efter behov.
    Unspecified // ”ikke angivet”: Anvendes for doseringer oprettet gennem versioner før FMK 1.3 / 1.4, og hvor det ikke er muligt at bestemme typen, dvs. at doseringen er som fritekst eller som ”efter skema i lokalt system”. Der kan ikke oprettes doseringer med typen ”ikke angivet” via FMK 1.3 / 1.4.
	
}
