CREATE TABLE IF NOT EXISTS `Folder`
(
    `user_id` INTEGER NOT NULL,
    `local_id` INTEGER NOT NULL,
    `name` TEXT NOT NULL,
    `parent` INTEGER,
    `created_at` INTEGER NOT NULL,
    `updated_at` INTEGER NOT NULL,
    PRIMARY KEY(`user_id`, `local_id`)
);
CREATE TABLE IF NOT EXISTS `Note`
(
    `user_id` INTEGER NOT NULL,
    `local_id` INTEGER NOT NULL,
    `title` TEXT NOT NULL,
    `content` TEXT NOT NULL,
    `notify` BIT NOT NULL,
    `notify_at` INTEGER,
    `folder_id` INTEGER,
    `created_at` INTEGER NOT NULL,
    `updated_at` INTEGER NOT NULL,
    PRIMARY KEY (`user_id`, `local_id`)
);
CREATE TABLE IF NOT EXISTS `Tag`
(
    `user_id` INTEGER NOT NULL,
    `local_id` INTEGER NOT NULL,
    `name` TEXT NOT NULL,
    `created_at` INTEGER NOT NULL,
    `updated_at` INTEGER NOT NULL,
    PRIMARY KEY(`user_id`, `local_id`)
);
CREATE TABLE IF NOT EXISTS `TagNoteCrossRef`
(
    `user_id` INTEGER NOT NULL,
    `tag_id` INTEGER NOT NULL,
    `note_id` INTEGER NOT NULL,
    `created_at` INTEGER NOT NULL,
    `updated_at` INTEGER NOT NULL
);