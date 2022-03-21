CREATE TABLE IF NOT EXISTS `Folder`
(
    `userId` INTEGER NOT NULL,
    `name` TEXT NOT NULL,
    `parent` INTEGER,
    `id` INTEGER PRIMARY KEY NOT NULL,
    `createdTime` DATE NOT NULL,
    `updatedTime` DATE NOT NULL,
    FOREIGN KEY(`parent`) REFERENCES `Folder`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS `Note`
(
    `userId` INTEGER NOT NULL,
    `title` TEXT NOT NULL,
    `content` TEXT NOT NULL,
    `notify` INTEGER NOT NULL,
    `folderId` INTEGER,
    `createdTime` DATE NOT NULL,
    `updatedTime` DATE NOT NULL,
    `id` INTEGER PRIMARY KEY NOT NULL,
    FOREIGN KEY(`folderId`) REFERENCES `Folder`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL
);
CREATE TABLE IF NOT EXISTS `Tag`
(
    `userId` INTEGER NOT NULL,
    `name` TEXT NOT NULL,
    `createdTime` DATE NOT NULL,
    `updatedTime` DATE NOT NULL,
    `id` INTEGER PRIMARY KEY NOT NULL
);
CREATE TABLE IF NOT EXISTS `TagNoteCrossRef`
(
    `userId` INTEGER NOT NULL,
    `tagId` INTEGER NOT NULL,
    `noteId` INTEGER NOT NULL,
    `createdTime` DATE NOT NULL,
    `updatedTime` DATE NOT NULL,
    PRIMARY KEY(`tagId`, `noteId`),
    FOREIGN KEY(`tagId`) REFERENCES `Tag`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE,
    FOREIGN KEY(`noteId`) REFERENCES `Note`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
);