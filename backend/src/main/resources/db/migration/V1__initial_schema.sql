-- Users table
CREATE TABLE users
(
    id            uuid PRIMARY KEY,
    email         varchar(255) NOT NULL UNIQUE,
    password_hash varchar(255),
    enabled       boolean      NOT NULL DEFAULT FALSE,
    role          varchar(50)  NOT NULL,
    created_at    timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE users IS 'Table for storing user authentication details';
COMMENT ON COLUMN users.id IS 'Unique identifier for each user';
COMMENT ON COLUMN users.email IS 'User''s email address, must be unique';
COMMENT ON COLUMN users.password_hash IS 'Hashed password for user authentication';
COMMENT ON COLUMN users.enabled IS 'Default is false. Automatically set to true after user verification';
COMMENT ON COLUMN users.role IS 'Role assigned to the user (e.g., admin, editor, user)';
COMMENT ON COLUMN users.created_at IS 'Timestamp when the user was created';
COMMENT ON COLUMN users.updated_at IS 'Timestamp when the user was last updated';

-- Social Connections table
CREATE TABLE social_connections
(
    user_id          uuid REFERENCES users (id),
    provider         varchar(50)  NOT NULL,
    provider_user_id varchar(255) NOT NULL,
    created_at       timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, provider),
    CONSTRAINT unique_provider_user UNIQUE (provider, provider_user_id)
);

COMMENT ON TABLE social_connections IS 'Table for storing social login connections';
COMMENT ON COLUMN social_connections.user_id IS 'Reference to the user in the users table';
COMMENT ON COLUMN social_connections.provider IS 'Social provider (e.g., Google, GitHub)';
COMMENT ON COLUMN social_connections.provider_user_id IS 'User ID provided by the social provider';
COMMENT ON COLUMN social_connections.created_at IS 'Timestamp when the social connection was created';

-- Refresh Tokens table
CREATE TABLE refresh_tokens
(
    id         uuid PRIMARY KEY,
    user_id    uuid REFERENCES users (id),
    token      varchar(255) NOT NULL UNIQUE,
    expires_at timestamp    NOT NULL,
    created_at timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE refresh_tokens IS 'Table for storing refresh tokens for authentication';
COMMENT ON COLUMN refresh_tokens.id IS 'Unique identifier for each refresh token';
COMMENT ON COLUMN refresh_tokens.user_id IS 'Reference to the user in the users table';
COMMENT ON COLUMN refresh_tokens.token IS 'Unique refresh token string';
COMMENT ON COLUMN refresh_tokens.expires_at IS 'Expiry time of the refresh token';
COMMENT ON COLUMN refresh_tokens.created_at IS 'Timestamp when the refresh token was created';

-- Media table
CREATE TABLE medias
(
    id          uuid PRIMARY KEY,
    file_path   varchar(255) NOT NULL,
    file_type   varchar(50)  NOT NULL,
    file_size   bigint       NOT NULL,
    title       varchar(255),
    alt_text    varchar(255),
    uploaded_by uuid REFERENCES users (id),
    created_at  timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE medias IS 'Table for storing media files like images and videos';

COMMENT ON COLUMN medias.id IS 'Unique identifier for each media file';
COMMENT ON COLUMN medias.file_path IS 'Path to the media file';
COMMENT ON COLUMN medias.file_type IS 'Type of the media file (e.g., image, video)';
COMMENT ON COLUMN medias.file_size IS 'Size of the media file in bytes';
COMMENT ON COLUMN medias.title IS 'Title of the media file';
COMMENT ON COLUMN medias.alt_text IS 'Alternative text for the media file, used for accessibility';
COMMENT ON COLUMN medias.uploaded_by IS 'Reference to the user who uploaded the media';
COMMENT ON COLUMN medias.created_at IS 'Timestamp when the media was uploaded';

-- Posts table
CREATE TABLE posts
(
    id                uuid PRIMARY KEY,
    title             varchar(255) NOT NULL,
    slug              varchar(255) NOT NULL UNIQUE,
    content           text NOT NULL,
    excerpt           varchar(255) NOT NULL,
    status            varchar(10)  NOT NULL,
    featured_image_id uuid REFERENCES medias (id),
    meta_title        varchar(255),
    meta_description  varchar(255),
    meta_keywords     varchar(255),
    og_title          varchar(255),
    og_description    varchar(255),
    created_at        timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE posts IS 'Table for storing blog posts and related content';

COMMENT ON COLUMN posts.id IS 'Unique identifier for each post';
COMMENT ON COLUMN posts.title IS 'Title of the post';
COMMENT ON COLUMN posts.slug IS 'Unique slug for the post, used in URLs';
COMMENT ON COLUMN posts.content IS 'Content of the post';
COMMENT ON COLUMN posts.excerpt IS 'Short excerpt of the post';
COMMENT ON COLUMN posts.status IS 'Status of the post (e.g., DRAFT, PUBLISHED)';
COMMENT ON COLUMN posts.featured_image_id IS 'Reference to the featured image in the media table, if applicable';
COMMENT ON COLUMN posts.meta_title IS 'SEO title for the post';
COMMENT ON COLUMN posts.meta_description IS 'SEO description for the post';
COMMENT ON COLUMN posts.meta_keywords IS 'SEO keywords for the post';
COMMENT ON COLUMN posts.og_title IS 'Open Graph title for social sharing';
COMMENT ON COLUMN posts.og_description IS 'Open Graph description for social sharing';
COMMENT ON COLUMN posts.created_at IS 'Timestamp when the post was created';
COMMENT ON COLUMN posts.updated_at IS 'Timestamp when the post was last updated, automatically updated on modification';

-- Categories table
CREATE TABLE categories
(
    id            uuid PRIMARY KEY,
    name          varchar(255) NOT NULL,
    slug          varchar(255) NOT NULL UNIQUE,
    description   varchar(255),
    parent_id     uuid REFERENCES categories (id),
    created_at    timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE categories IS 'Table for storing categories';

COMMENT ON COLUMN categories.id IS 'Unique identifier for each category';
COMMENT ON COLUMN categories.name IS 'Name of the category';
COMMENT ON COLUMN categories.slug IS 'Unique slug for the category, used in URLs';
COMMENT ON COLUMN categories.description IS 'Description of the category';
COMMENT ON COLUMN categories.parent_id IS 'Reference to the parent category for hierarchical categorization';
COMMENT ON COLUMN categories.created_at IS 'Timestamp when the category was created';


-- Post-Categories Relationship table
CREATE TABLE post_categories
(
    post_id     uuid REFERENCES posts (id),
    category_id uuid REFERENCES categories (id),
    PRIMARY KEY (post_id, category_id)
);

COMMENT ON TABLE post_categories IS 'Table for storing many-to-many relationships between posts and categories';
COMMENT ON COLUMN post_categories.post_id IS 'Reference to the post in the posts table';
COMMENT ON COLUMN post_categories.category_id IS 'Reference to the category in the category table';

-- Indexes
CREATE INDEX idx_posts_slug ON posts (slug);
CREATE INDEX idx_posts_status ON posts (status);
CREATE INDEX idx_categories_slug ON categories (slug);
CREATE INDEX idx_post_categories_category ON post_categories (category_id);
CREATE INDEX idx_post_categories_post ON post_categories (post_id);

-- Insert the specified data into the categories table
INSERT INTO categories (
    id,
    name,
    slug,
    description,
    parent_id,
    created_at
)
VALUES (
    '01939280-7ccb-72a8-9257-7ba44de715b6',
    '未設定',
    'nosetting',
    '未設定カテゴリ',
    NULL,
    CURRENT_TIMESTAMP
);

