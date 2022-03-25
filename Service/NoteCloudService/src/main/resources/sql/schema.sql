CREATE TABLE IF NOT EXISTS `Folder`
(
    `user_id` INTEGER NOT NULL,
    `id` INTEGER NOT NULL,
    `name` TEXT NOT NULL,
    `parent` INTEGER,
    `created_at` BIGINT NOT NULL,
    `updated_at` BIGINT NOT NULL,
    PRIMARY KEY(`user_id`, `id`)
);
CREATE TABLE IF NOT EXISTS `Note`
(
    `user_id` INTEGER NOT NULL,
    `id` INTEGER NOT NULL,
    `title` TEXT NOT NULL,
    `content` TEXT NOT NULL,
    `notify` BIT NOT NULL,
    `notify_at` BIGINT,
    `folder_id` INTEGER,
    `created_at` BIGINT NOT NULL,
    `updated_at` BIGINT NOT NULL,
    PRIMARY KEY (`user_id`, `id`)
);
CREATE TABLE IF NOT EXISTS `Tag`
(
    `user_id` INTEGER NOT NULL,
    `id` INTEGER NOT NULL,
    `name` TEXT NOT NULL,
    `created_at` BIGINT NOT NULL,
    `updated_at` BIGINT NOT NULL,
    PRIMARY KEY(`user_id`, `id`)
);
CREATE TABLE IF NOT EXISTS `TagNoteCrossRef`
(
    `user_id` INTEGER NOT NULL,
    `tag_id` INTEGER NOT NULL,
    `note_id` INTEGER NOT NULL,
    `created_at` BIGINT NOT NULL,
    `updated_at` BIGINT NOT NULL
);
CREATE TABLE IF NOT EXISTS `DeleteLog`
(
    `user_id` INTEGER NOT NULL,
    `id` INTEGER NOT NULL,
    `table_name` TEXT NOT NULL,
    `id_primary` INTEGER NOT NULL,
    `id_secondary` INTEGER,
    `deleted_at` BIGINT NOT NULL,
    PRIMARY KEY(`user_id`, `id`)
)