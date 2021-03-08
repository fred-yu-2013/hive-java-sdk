package org.elastos.hive.vault;

import org.elastos.hive.Vault;
import org.elastos.hive.service.BackupService;
import org.elastos.hive.service.Database;
import org.elastos.hive.service.FilesService;
import org.elastos.hive.service.PubsubService;

public class ServiceBuilder {
	private Vault vault;

	public ServiceBuilder(Vault vault) {
		this.vault = vault;
	}

	public FilesService createFilesService() {
		return (FilesService) new BackupServiceRender(vault);
	}

	public Database createDatabase() {
		return new DatabaseRender(vault);
	}

	public PubsubService createPubsubService() {
		return new PubsubServiceRender(vault);
	}

	public BackupService createBackupService() {
		return new BackupServiceRender(vault);
	}

	public FilesService createFileService() {
		// TODO Auto-generated method stub
		return null;
	}
}
