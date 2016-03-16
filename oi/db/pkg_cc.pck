create or replace package pkg_cc is

  procedure pro_insert_session(i_create_user_id   cc_session.create_user_id%type,
                               i_create_user_name cc_session.create_user_name%type,
                               i_create_time      cc_session.create_time%type,
                               i_session_id       out cc_session.session_id%type);

  procedure pro_upd_session_updtime;

end pkg_cc;
/
create or replace package body pkg_cc is

  procedure pro_insert_session(i_create_user_id   cc_session.create_user_id%type,
                               i_create_user_name cc_session.create_user_name%type,
                               i_create_time      cc_session.create_time%type,
                               i_session_id       out cc_session.session_id%type) is
  
  begin
    insert into cc_session s
      (session_id,
       create_time,
       s.create_user_id,
       s.create_user_name,
       s.update_time)
    values
      (cc_session_seq.nextval,
       i_create_time,
       i_create_user_id,
       i_create_user_name,
       i_create_time)
    returning session_id into i_session_id;
  
    commit;
  
  end pro_insert_session;

  procedure pro_upd_session_updtime is
    v_seesion_user_count number := 0;
  
  begin
    for c_session in (select session_id,
                             create_time,
                             create_user_id,
                             create_user_name,
                             update_time
                        from cc_session ccs
                       where ccs.update_time >= sysdate - interval '6' hour
                       order by ccs.update_time) loop
    
      select count(1)
        into v_seesion_user_count
        from cc_session_messages csm
       where csm.session_id = c_session.session_id;
    
      if v_seesion_user_count > 0 then
        update cc_session css
           set css.update_time =
               (select sub.send_time
                  from (select csm.send_time
                          from cc_session_messages csm
                         where csm.session_id = c_session.session_id
                         order by csm.send_time desc) sub
                 where rownum < 2)
         where css.session_id = c_session.session_id;
        commit;
      end if;
    end loop;
  
  end pro_upd_session_updtime;
end pkg_cc;
/
