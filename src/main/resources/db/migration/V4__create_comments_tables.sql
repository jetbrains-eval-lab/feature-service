-- Create sequence for comments
create sequence comment_id_seq start with 100 increment by 50;

-- Create comments table
create table comments(
    id bigint not null primary key default nextval('comment_id_seq'),
    text text not null,
    created_at timestamp not null default current_timestamp,
    author varchar(255) not null,
    feature_id bigint references features(id),
    release_id bigint references releases(id),
    parent_id bigint references comments(id)
);

-- Create indexes for better performance
create index idx_comments_release_id on comments(release_id);
create index idx_comments_parent_id on comments(parent_id);