CREATE TABLE IF NOT EXISTS `Note`
(
    `title` TEXT NOT NULL,
    `content` TEXT NOT NULL,
    `notify` INTEGER NOT NULL,
    `folderId` INTEGER,
    `createdTime` DATE NOT NULL,
    `updatedTime` DATE NOT NULL,
    `id` INTEGER PRIMARY KEY NOT NULL,
    FOREIGN KEY(`folderId`) REFERENCES `Folder`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL
);
CREATE TABLE IF NOT EXISTS `Folder`
(
    `name` TEXT NOT NULL,
    `parent` INTEGER,
    `id` INTEGER PRIMARY KEY NOT NULL,
    `createdTime` DATE NOT NULL,
    `updatedTime` DATE NOT NULL,
    FOREIGN KEY(`parent`) REFERENCES `Folder`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS `Tag`
(
    `name` TEXT NOT NULL,
    `createdTime` DATE NOT NULL,
    `updatedTime` DATE NOT NULL,
    `id` INTEGER PRIMARY KEY NOT NULL
);
CREATE TABLE IF NOT EXISTS `TagNoteCrossRef`
(
    `tagId` INTEGER NOT NULL,
    `noteId` INTEGER NOT NULL,
    `createdTime` DATE NOT NULL,
    `updatedTime` DATE NOT NULL,
    PRIMARY KEY(`tagId`, `noteId`),
    FOREIGN KEY(`tagId`) REFERENCES `Tag`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE,
    FOREIGN KEY(`noteId`) REFERENCES `Note`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
);