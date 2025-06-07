create table if not exists credential(
    id bigint primary key generated always as identity,
    attestation_object bytea not null,
    client_data bytea not null,
    client_extensions text,
    transports bytea,
    credential_id text not null,
    current_challenge text,
    sign_count bigint not null default 0
);