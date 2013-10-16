package dk.medicinkortet.dosagetranslation.dumper;

import java.util.Date;

public class DumpVersion {

	public long releaseNumber;
	public Date releaseDate;
	public Date lmsDate;
	public Date daDate;
	
	public DumpVersion(long releaseNumber,	Date releaseDate, Date lmsDate, Date daDate) {
		this.releaseNumber = releaseNumber;
		this.releaseDate = releaseDate;
		this.lmsDate = lmsDate;
		this.daDate = daDate;
	}

}
