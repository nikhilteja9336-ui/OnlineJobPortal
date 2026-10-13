import {useState,useEffect} from "react";
import "./styles.css";
const API=import.meta.env.VITE_API_URL||"http://localhost:8080";
const call=async(p,o={},t)=>{
 const r=await fetch(API+"/api"+p,{...o,headers:{"Content-Type":"application/json",...(t?{Authorization:"Bearer "+t}:{})}});
 const x=await r.text(),d=x?JSON.parse(x):null;
 if(!r.ok)throw new Error(d?.message||"Request failed ("+r.status+")");return d;
};
const TYPES=["Full-time","Part-time","Internship","Contract","Remote"];
const Badge=({s})=><span className={"badge "+s}>{s}</span>;

export default function App(){
 const [auth,setAuth]=useState(()=>JSON.parse(localStorage.getItem("auth")||"null"));
 const [view,setView]=useState("home"),[job,setJob]=useState(null);
 const token=auth?.token,user=auth?.user;
 const login=a=>{localStorage.setItem("auth",JSON.stringify(a));setAuth(a);setView(a.user.role==="EMPLOYER"?"dash":"home")};
 const logout=()=>{localStorage.removeItem("auth");setAuth(null);setView("home")};
 return <>
  <header><b onClick={()=>setView("home")}>💼 JobPortal</b>
   <nav><button className="ghost" onClick={()=>setView("home")}>Jobs</button>
    {user?<><button className="ghost" onClick={()=>setView("dash")}>{user.role==="EMPLOYER"?"Employer Dashboard":"My Applications"}</button>
      <span className="muted">Hi, {user.name}</span><button onClick={logout}>Logout</button></>
    :<><button className="ghost" onClick={()=>setView("login")}>Login</button><button onClick={()=>setView("register")}>Sign up</button></>}</nav></header>
  {view==="home"&&<Home open={j=>{setJob(j);setView("job")}}/>}
  {view==="job"&&<JobView job={job} user={user} token={token} go={setView}/>}
  {(view==="login"||view==="register")&&<Auth mode={view} onAuth={login} go={setView}/>}
  {view==="dash"&&(user?.role==="EMPLOYER"?<Employer token={token}/>:user?<Seeker token={token}/>:<Auth mode="login" onAuth={login} go={setView}/>)}
 </>;
}

function Home({open}){
 const [f,setF]=useState({q:"",location:"",type:""}),[jobs,setJobs]=useState([]),[err,setErr]=useState("");
 const load=()=>call("/jobs?"+new URLSearchParams(f)).then(setJobs).catch(e=>setErr(e.message));
 useEffect(()=>{load()},[]);
 const s=k=>e=>setF({...f,[k]:e.target.value});
 return <><section className="hero"><h1>Find your next opportunity</h1>
  <form onSubmit={e=>{e.preventDefault();load()}}>
   <input placeholder="Job title or company" value={f.q} onChange={s("q")}/>
   <input placeholder="Location" value={f.location} onChange={s("location")}/>
   <select value={f.type} onChange={s("type")}><option value="">All types</option>{TYPES.map(t=><option key={t}>{t}</option>)}</select>
   <button>Search</button></form></section>
  <main>{err&&<p className="err">{err} (is the backend running?)</p>}
   <p className="muted">{jobs.length} jobs found</p>
   <div className="grid">{jobs.map(j=><div className="card" key={j.id} onClick={()=>open(j)}>
    <h3>{j.title}</h3><div className="muted">{j.company} · {j.location}</div>
    <span className="tag">{j.type}</span><span className="tag">{j.salary}</span>
    <p className="muted">Posted {j.postedAt}</p></div>)}</div></main></>;
}

function JobView({job,user,token,go}){
 const [cl,setCl]=useState(""),[msg,setMsg]=useState(""),[err,setErr]=useState("");
 const apply=async()=>{setErr("");setMsg("");try{await call(`/jobs/${job.id}/apply`,{method:"POST",body:JSON.stringify({coverLetter:cl})},token);setMsg("Application submitted!")}catch(e){setErr(e.message)}};
 return <main><button className="ghost" onClick={()=>go("home")}>← Back</button>
  <div className="card stack" style={{marginTop:12}}><h2 style={{margin:0}}>{job.title}</h2>
   <div className="muted">{job.company} · {job.location}</div>
   <div><span className="tag">{job.type}</span><span className="tag">{job.salary}</span></div>
   <p>{job.description}</p>
   {!user&&<button onClick={()=>go("login")}>Login to apply</button>}
   {user?.role==="EMPLOYER"&&<p className="muted">Log in as a job seeker to apply.</p>}
   {user?.role==="JOB_SEEKER"&&<><textarea rows="5" placeholder="Cover letter / why you're a great fit" value={cl} onChange={e=>setCl(e.target.value)}/>
    <button onClick={apply}>Apply now</button></>}
   {msg&&<p className="ok">{msg}</p>}{err&&<p className="err">{err}</p>}</div></main>;
}

function Auth({mode,onAuth,go}){
 const [f,setF]=useState({name:"",email:"",password:"",role:"JOB_SEEKER"}),[err,setErr]=useState("");
 const reg=mode==="register",s=k=>e=>setF({...f,[k]:e.target.value});
 const submit=async e=>{e.preventDefault();setErr("");try{onAuth(await call("/auth/"+mode,{method:"POST",body:JSON.stringify(f)}))}catch(x){setErr(x.message)}};
 return <form className="form" onSubmit={submit}><h2>{reg?"Create account":"Welcome back"}</h2>
  {reg&&<input placeholder="Full name" required value={f.name} onChange={s("name")}/>}
  <input type="email" placeholder="Email" required value={f.email} onChange={s("email")}/>
  <input type="password" placeholder="Password (6+ chars)" required value={f.password} onChange={s("password")}/>
  {reg&&<select value={f.role} onChange={s("role")}><option value="JOB_SEEKER">I'm looking for a job</option><option value="EMPLOYER">I'm hiring (Employer)</option></select>}
  <button>{reg?"Sign up":"Login"}</button>{err&&<p className="err">{err}</p>}
  <p className="muted">{reg?"Have an account? ":"New here? "}<a href="#" onClick={e=>{e.preventDefault();go(reg?"login":"register")}}>{reg?"Login":"Sign up"}</a></p>
  {!reg&&<p className="muted">Demo: employer@demo.com / seeker@demo.com — password demo123</p>}</form>;
}

function Seeker({token}){
 const [a,setA]=useState([]);
 useEffect(()=>{call("/applications/mine",{},token).then(setA)},[]);
 return <main><h2>My Applications</h2>{a.length===0&&<p className="muted">No applications yet.</p>}
  {a.map(x=><div className="card row" style={{marginBottom:12}} key={x.id}><div><b>{x.jobTitle}</b><div className="muted">{x.company} · applied {x.appliedAt}</div></div><Badge s={x.status}/></div>)}</main>;
}

function Employer({token}){
 const empty={title:"",company:"",location:"",type:"Full-time",salary:"",description:""};
 const [f,setF]=useState(empty),[jobs,setJobs]=useState([]),[open,setOpen]=useState({}),[err,setErr]=useState("");
 const load=()=>call("/jobs/mine",{},token).then(setJobs);
 useEffect(()=>{load()},[]);
 const s=k=>e=>setF({...f,[k]:e.target.value});
 const post=async e=>{e.preventDefault();setErr("");try{await call("/jobs",{method:"POST",body:JSON.stringify(f)},token);setF(empty);load()}catch(x){setErr(x.message)}};
 const apps=async id=>setOpen({...open,[id]:open[id]?null:await call(`/jobs/${id}/applications`,{},token)});
 const setSt=async(a,st)=>{await call(`/applications/${a.id}/status`,{method:"PUT",body:JSON.stringify({status:st})},token);setOpen({...open,[a.jobId]:await call(`/jobs/${a.jobId}/applications`,{},token)})};
 const del=async id=>{if(confirm("Delete this job and its applications?")){await call("/jobs/"+id,{method:"DELETE"},token);load()}};
 return <main><h2>Post a new job</h2>
  <form className="card stack" onSubmit={post}>
   <input placeholder="Job title" required value={f.title} onChange={s("title")}/>
   <input placeholder="Company" required value={f.company} onChange={s("company")}/>
   <input placeholder="Location" required value={f.location} onChange={s("location")}/>
   <select value={f.type} onChange={s("type")}>{TYPES.map(t=><option key={t}>{t}</option>)}</select>
   <input placeholder="Salary (e.g. ₹10-15 LPA)" value={f.salary} onChange={s("salary")}/>
   <textarea rows="4" placeholder="Job description" required value={f.description} onChange={s("description")}/>
   <button>Publish job</button>{err&&<p className="err">{err}</p>}</form>
  <h2>My jobs</h2>
  {jobs.map(j=><div className="card stack" style={{marginBottom:12}} key={j.id}>
   <div className="row"><div><b>{j.title}</b><div className="muted">{j.company} · {j.location} · {j.type}</div></div>
    <div><button className="sm ghost" onClick={()=>apps(j.id)}>Applicants</button> <button className="sm red" onClick={()=>del(j.id)}>Delete</button></div></div>
   {open[j.id]&&(open[j.id].length===0?<p className="muted">No applicants yet.</p>:open[j.id].map(a=>
    <div key={a.id} className="card"><div className="row"><div><b>{a.seekerName}</b> <span className="muted">{a.seekerEmail}</span></div><Badge s={a.status}/></div>
     <p className="muted">{a.coverLetter||"(no cover letter)"}</p>
     <button className="sm" onClick={()=>setSt(a,"SHORTLISTED")}>Shortlist</button> <button className="sm red" onClick={()=>setSt(a,"REJECTED")}>Reject</button></div>))}</div>)}
 </main>;
}
