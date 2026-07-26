import { lazy, Suspense, useMemo, useState } from "react";
import { Navigate, Route, Routes, useLocation, useNavigate } from "react-router-dom";
import {
  Bell, Building2, CalendarClock, Car, ChevronDown, ChevronRight, CircleDollarSign,
  ClipboardCheck, Download, FileBarChart, HeartPulse, LayoutDashboard, LifeBuoy,
  LogOut, Menu, MoreHorizontal, Plus, Search, Send, Settings, ShieldCheck, Sparkles,
  TrendingUp, UserRound, Users, X
} from "lucide-react";
import { Bar, BarChart, CartesianGrid, Cell, Pie, PieChart, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts";
import { api } from "./api";
import { companyLabel, customers, inr, policies, revenue, vehicles } from "./data";

const remoteDashboard = lazy(() => import("dashboard/DashboardPage"));
const remoteCustomers = lazy(() => import("customers/CustomersApp"));
const remotePolicies = lazy(() => import("policies/PoliciesApp"));
const remoteVehicles = lazy(() => import("vehicles/VehiclesApp"));
const remoteNotifications = lazy(() => import("notifications/NotificationsApp"));
const remoteReports = lazy(() => import("reports/ReportsApp"));
const remotePortal = lazy(() => import("portal/CustomerPortalApp"));

type NavItem = { path: string; label: string; icon: typeof LayoutDashboard };
const nav: NavItem[] = [
  { path: "/", label: "Overview", icon: LayoutDashboard },
  { path: "/customers", label: "Customers", icon: Users },
  { path: "/policies", label: "Policies", icon: ShieldCheck },
  { path: "/vehicles", label: "Vehicles", icon: Car },
  { path: "/notifications", label: "Reminders", icon: Bell },
  { path: "/reports", label: "Reports", icon: FileBarChart }
];

export default function App() {
  const [authenticated, setAuthenticated] = useState(() => Boolean(localStorage.getItem("insuredesk-session")));
  const [collapsed, setCollapsed] = useState(false);
  const [mobileOpen, setMobileOpen] = useState(false);
  if (!authenticated) return <Login onLogin={() => setAuthenticated(true)} />;
  return (
    <div className={`app-frame ${collapsed ? "nav-collapsed" : ""}`}>
      <Sidebar collapsed={collapsed} mobileOpen={mobileOpen} closeMobile={() => setMobileOpen(false)} onLogout={() => {
        localStorage.removeItem("insuredesk-session"); localStorage.removeItem("insuredesk-token"); setAuthenticated(false);
      }} />
      <main className="main">
        <Topbar toggleNav={() => setCollapsed((v) => !v)} openMobile={() => setMobileOpen(true)} />
        <div className="page-wrap">
          <Routes>
            <Route path="/" element={<RemoteSwitch local={<Dashboard />} remote={remoteDashboard} />} />
            <Route path="/customers" element={<RemoteSwitch local={<Customers />} remote={remoteCustomers} />} />
            <Route path="/policies" element={<RemoteSwitch local={<Policies />} remote={remotePolicies} />} />
            <Route path="/vehicles" element={<RemoteSwitch local={<Vehicles />} remote={remoteVehicles} />} />
            <Route path="/notifications" element={<RemoteSwitch local={<Notifications />} remote={remoteNotifications} />} />
            <Route path="/reports" element={<RemoteSwitch local={<Reports />} remote={remoteReports} />} />
            <Route path="/portal" element={<RemoteSwitch local={<Portal />} remote={remotePortal} />} />
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </div>
      </main>
      {mobileOpen && <button className="scrim" onClick={() => setMobileOpen(false)} aria-label="Close navigation" />}
    </div>
  );
}

function RemoteSwitch({ local, remote: Remote }: { local: React.ReactNode; remote: React.ComponentType }) {
  const useRemotes = import.meta.env.VITE_USE_REMOTE_MFES === "true";
  if (!useRemotes) return <>{local}</>;
  return <Suspense fallback={<div className="remote-loading"><div className="spinner" /> Loading workspace module…</div>}><Remote /></Suspense>;
}

function Login({ onLogin }: { onLogin: () => void }) {
  const [email, setEmail] = useState("agent@insuredesk.local");
  const [password, setPassword] = useState("password");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const submit = async (e: React.FormEvent) => {
    e.preventDefault(); setLoading(true); setError("");
    try {
      const response = await api.post("/auth/login", { email, password });
      localStorage.setItem("insuredesk-token", response.data.data.accessToken);
    } catch {
      if (import.meta.env.VITE_ENABLE_DEMO_DATA !== "true") { setError("Could not sign in. Check your email, password, and API connection."); setLoading(false); return; }
    }
    localStorage.setItem("insuredesk-session", "dealer"); setLoading(false); onLogin();
  };
  const demo = () => { localStorage.setItem("insuredesk-session", "demo"); onLogin(); };
  return (
    <div className="login-page">
      <div className="login-brand">
        <div className="brand-mark large"><ShieldCheck /></div>
        <span>InsureDesk</span>
      </div>
      <section className="login-story">
        <div className="story-copy">
          <span className="eyebrow light"><Sparkles size={14} /> Built for independent advisors</span>
          <h1>Every policy.<br />Every promise.<br /><em>Beautifully managed.</em></h1>
          <p>One calm workspace for your customers, renewals, vehicles, claims, commissions and every follow-up in between.</p>
          <div className="trust-row"><span>Star Health</span><span>Tata AIG</span><span>LIC</span><span>IFFCO Tokio</span></div>
        </div>
      </section>
      <section className="login-panel">
        <div className="login-card">
          <span className="eyebrow">Advisor workspace</span>
          <h2>Welcome back</h2>
          <p className="muted">Sign in to continue to your insurance desk.</p>
          <form onSubmit={submit}>
            <label>Email address<input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required /></label>
            <label>Password<div className="password-wrap"><input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required /></div></label>
            {error && <div className="form-error">{error}</div>}
            <div className="form-meta"><label className="check"><input type="checkbox" defaultChecked /> Keep me signed in</label><a href="#forgot">Forgot password?</a></div>
            <button className="button primary full" disabled={loading}>{loading ? "Signing in…" : "Sign in securely"} <ChevronRight size={17} /></button>
          </form>
          <button className="demo-link" onClick={demo}>Explore with demo data</button>
          <p className="security-note"><ShieldCheck size={14} /> Protected with encrypted, role-based access</p>
        </div>
      </section>
    </div>
  );
}

function Sidebar({ collapsed, mobileOpen, closeMobile, onLogout }: { collapsed: boolean; mobileOpen: boolean; closeMobile: () => void; onLogout: () => void }) {
  const location = useLocation(); const navigate = useNavigate();
  return (
    <aside className={`sidebar ${mobileOpen ? "mobile-open" : ""}`}>
      <div className="brand"><div className="brand-mark"><ShieldCheck /></div><span>InsureDesk</span><button className="mobile-close" onClick={closeMobile}><X /></button></div>
      <div className="workspace-chip"><div className="avatar">KI</div><div><strong>Karthik Insurance</strong><span>Dealer workspace</span></div><ChevronDown size={15} /></div>
      <nav>
        <span className="nav-label">Workspace</span>
        {nav.map((item) => <button key={item.path} className={location.pathname === item.path ? "active" : ""} onClick={() => { navigate(item.path); closeMobile(); }} title={collapsed ? item.label : undefined}><item.icon /><span>{item.label}</span>{item.label === "Reminders" && <b>8</b>}</button>)}
        <span className="nav-label second">Customer access</span>
        <button className={location.pathname === "/portal" ? "active" : ""} onClick={() => navigate("/portal")}><UserRound /><span>Customer portal</span></button>
      </nav>
      <div className="sidebar-foot">
        <button><Settings /><span>Settings</span></button>
        <button onClick={onLogout}><LogOut /><span>Sign out</span></button>
        <div className="advisor"><div className="avatar warm">KA</div><div><strong>Karthik</strong><span>Insurance advisor</span></div></div>
      </div>
    </aside>
  );
}

function Topbar({ toggleNav, openMobile }: { toggleNav: () => void; openMobile: () => void }) {
  return (
    <header className="topbar">
      <button className="icon-button desktop-menu" onClick={toggleNav}><Menu /></button>
      <button className="icon-button mobile-menu" onClick={openMobile}><Menu /></button>
      <div className="global-search"><Search /><input placeholder="Search customers, policies, vehicles…" /><kbd>⌘ K</kbd></div>
      <div className="top-actions"><span className="system-good"><i /> All systems healthy</span><button className="icon-button bell"><Bell /><b>8</b></button><button className="button primary compact"><Plus size={16} /> Add policy</button></div>
    </header>
  );
}

function PageHeader({ eyebrow, title, description, action }: { eyebrow: string; title: string; description: string; action?: React.ReactNode }) {
  return <div className="page-header"><div><span className="eyebrow">{eyebrow}</span><h1>{title}</h1><p>{description}</p></div>{action}</div>;
}

function Dashboard() {
  const chartColors = ["#146b59", "#d5a33f", "#507aa5"];
  return (
    <>
      <PageHeader eyebrow="Sunday, 26 July" title="Good morning, Karthik." description="Here’s what needs your attention across the business today." action={<button className="button subtle"><CalendarClock size={17} /> July 2026 <ChevronDown size={15} /></button>} />
      <section className="attention-strip"><div className="attention-icon"><Bell /></div><div><strong>7 policies need attention this week</strong><span>₹2.84 lakh in premium is due for renewal</span></div><div className="stacked-avatars"><i>AM</i><i>MN</i><i>PR</i><i>+4</i></div><button>Review renewals <ChevronRight size={16} /></button></section>
      <div className="stats-grid">
        <Stat icon={ShieldCheck} tone="green" label="Active policies" value="286" meta="↑ 12 this month" />
        <Stat icon={CalendarClock} tone="amber" label="Expiring in 15 days" value="18" meta="₹6.2L premium at risk" />
        <Stat icon={CircleDollarSign} tone="blue" label="July commission" value="₹1.86L" meta="↑ 14.2% vs June" />
        <Stat icon={Users} tone="violet" label="Total customers" value="172" meta="8 joined this month" />
      </div>
      <div className="content-grid">
        <section className="card span-2">
          <CardHead title="Premium performance" subtitle="Premium collected over the last six months" action={<button className="text-button">View report <ChevronRight /></button>} />
          <div className="chart-summary"><strong>₹27.7L</strong><span>Total premium</span><b><TrendingUp size={14} /> 18.4%</b></div>
          <div className="chart-wrap"><ResponsiveContainer width="100%" height="100%"><BarChart data={revenue} barGap={5}><CartesianGrid vertical={false} stroke="#edf0ec" /><XAxis dataKey="month" axisLine={false} tickLine={false} /><YAxis axisLine={false} tickLine={false} tickFormatter={(v) => `₹${v}L`} /><Tooltip cursor={{fill:"#f4f7f4"}} formatter={(v) => `₹${v}L`} /><Bar dataKey="premium" fill="#146b59" radius={[5,5,0,0]} barSize={24} /><Bar dataKey="commission" fill="#d8e6df" radius={[5,5,0,0]} barSize={12} /></BarChart></ResponsiveContainer></div>
        </section>
        <section className="card">
          <CardHead title="Policy mix" subtitle="Active book by category" />
          <div className="donut"><ResponsiveContainer width="100%" height="100%"><PieChart><Pie data={[{name:"Health",value:146},{name:"Life",value:82},{name:"Vehicle",value:58}]} dataKey="value" innerRadius={58} outerRadius={78} paddingAngle={3}>{chartColors.map((c)=><Cell key={c} fill={c}/>)}</Pie><Tooltip /></PieChart></ResponsiveContainer><div><strong>286</strong><span>policies</span></div></div>
          <div className="legend"><span><i style={{background:chartColors[0]}} />Health <b>51%</b></span><span><i style={{background:chartColors[1]}} />Life <b>29%</b></span><span><i style={{background:chartColors[2]}} />Vehicle <b>20%</b></span></div>
        </section>
        <section className="card span-2">
          <CardHead title="Upcoming renewals" subtitle="Policies closest to expiry" action={<button className="text-button">View all <ChevronRight /></button>} />
          <div className="table-scroll"><table><thead><tr><th>Customer</th><th>Policy</th><th>Provider</th><th>Expires</th><th>Time left</th><th /></tr></thead><tbody>{policies.slice(0,4).map((p)=><tr key={p.id}><td><div className="person"><span>{initials(p.customer)}</span><strong>{p.customer}</strong></div></td><td><strong>{p.plan}</strong><small>{p.number}</small></td><td><Company value={p.company}/></td><td>{date(p.expiry)}</td><td><Days days={p.days}/></td><td><button className="row-action"><MoreHorizontal /></button></td></tr>)}</tbody></table></div>
        </section>
        <section className="card">
          <CardHead title="Today’s follow-ups" subtitle="4 conversations due" />
          <div className="followups">
            <Follow name="Priya Raman" note="Discuss family floater upgrade" time="10:30 AM" lead="HOT" />
            <Follow name="Vikram Shah" note="Vehicle renewal documents" time="12:15 PM" lead="WARM" />
            <Follow name="Meera Nair" note="LIC premium receipt pending" time="4:00 PM" lead="WARM" />
          </div>
          <button className="button subtle full">Open follow-up board</button>
        </section>
      </div>
    </>
  );
}

function Customers() {
  const [query, setQuery] = useState(""); const [drawer, setDrawer] = useState(false);
  const filtered = customers.filter((c) => `${c.name} ${c.phone} ${c.email}`.toLowerCase().includes(query.toLowerCase()));
  return <>
    <PageHeader eyebrow="Relationships" title="Customers" description="A complete view of every customer and the protection they hold." action={<button className="button primary" onClick={() => setDrawer(true)}><Plus size={17}/> New customer</button>} />
    <section className="card table-card">
      <div className="table-toolbar"><div className="search-box"><Search/><input value={query} onChange={(e)=>setQuery(e.target.value)} placeholder="Search by name, phone or email"/></div><div><button className="button subtle">All policy types <ChevronDown size={15}/></button><button className="icon-button"><Settings/></button></div></div>
      <div className="table-scroll"><table><thead><tr><th>Customer</th><th>Contact</th><th>City</th><th>Active policies</th><th>Annual premium</th><th>Lead</th><th/></tr></thead><tbody>{filtered.map((c)=><tr key={c.id}><td><div className="person"><span>{initials(c.name)}</span><strong>{c.name}</strong></div></td><td><strong>{c.phone}</strong><small>{c.email}</small></td><td>{c.city}</td><td><b className="count-badge">{c.policies}</b></td><td><strong>{inr(c.premium)}</strong></td><td><span className={`lead ${c.lead.toLowerCase()}`}>{c.lead}</span></td><td><button className="row-action"><MoreHorizontal/></button></td></tr>)}</tbody></table></div>
      <div className="pagination"><span>Showing 1–{filtered.length} of 172 customers</span><div><button disabled>Previous</button><button className="selected">1</button><button>2</button><button>3</button><button>Next</button></div></div>
    </section>
    {drawer && <Drawer title="Add a new customer" subtitle="Start with their essential contact details." close={()=>setDrawer(false)}><CustomerForm close={()=>setDrawer(false)}/></Drawer>}
  </>;
}

function CustomerForm({close}:{close:()=>void}) {
  return <form className="drawer-form" onSubmit={(e)=>{e.preventDefault();close();}}>
    <div className="step-line"><span className="active">1</span><i/><span>2</span><i/><span>3</span></div>
    <div className="form-grid"><label className="wide">Full name<input required placeholder="e.g. Ramesh Kumar"/></label><label>Mobile number<input required placeholder="+91 98765 43210"/></label><label>Email address<input type="email" placeholder="name@example.com"/></label><label>Date of birth<input type="date"/></label><label>City<input placeholder="Chennai"/></label><label className="wide">Address<textarea rows={3} placeholder="Street, locality and pincode"/></label><label className="wide">Notes<textarea rows={3} placeholder="Anything useful for the next conversation"/></label></div>
    <div className="drawer-actions"><button type="button" className="button subtle" onClick={close}>Cancel</button><button className="button primary">Save & continue <ChevronRight size={16}/></button></div>
  </form>;
}

function Policies() {
  const [wizard,setWizard]=useState(false);
  return <>
    <PageHeader eyebrow="Portfolio" title="Policies" description="Track coverage, premiums, commissions and every renewal in one place." action={<button className="button primary" onClick={()=>setWizard(true)}><Plus size={17}/> Add policy</button>} />
    <div className="mini-stats"><span><b>286</b> Active</span><span><b>18</b> Expiring soon</span><span><b>7</b> Lapsed</span><span><b>₹48.6L</b> Book premium</span></div>
    <section className="card table-card">
      <div className="table-toolbar"><div className="search-box"><Search/><input placeholder="Search policy number or customer"/></div><div><button className="button subtle">Company <ChevronDown size={15}/></button><button className="button subtle">Status <ChevronDown size={15}/></button><button className="button subtle"><Download size={16}/> Export</button></div></div>
      <div className="table-scroll"><table><thead><tr><th>Customer</th><th>Policy</th><th>Provider</th><th>Premium</th><th>Expiry</th><th>Status</th><th/></tr></thead><tbody>{policies.map((p)=><tr key={p.id}><td><div className="person"><span>{initials(p.customer)}</span><strong>{p.customer}</strong></div></td><td><strong>{p.plan}</strong><small>{p.number}</small></td><td><Company value={p.company}/></td><td><strong>{inr(p.premium)}</strong><small>Yearly</small></td><td>{date(p.expiry)}<small><Days days={p.days}/></small></td><td><span className={`status ${p.status.toLowerCase()}`}>{p.status}</span></td><td><button className="row-action"><MoreHorizontal/></button></td></tr>)}</tbody></table></div>
    </section>
    {wizard && <Drawer title="Add a policy" subtitle="A guided flow that adapts to the insurer and policy type." close={()=>setWizard(false)} wide><PolicyWizard close={()=>setWizard(false)}/></Drawer>}
  </>;
}

function PolicyWizard({close}:{close:()=>void}) {
  const [step,setStep]=useState(1); const [type,setType]=useState("HEALTH");
  const labels=["Policy type","Provider","Policy details","Review"];
  return <div className="wizard"><div className="wizard-steps">{labels.map((l,i)=><div className={step>=i+1?"done":""} key={l}><span>{step>i+1?"✓":i+1}</span><b>{l}</b></div>)}</div>
    <div className="wizard-body">
      {step===1&&<><h3>What kind of protection is this?</h3><p className="muted">Choose a category to reveal the right policy fields.</p><div className="type-cards">{[["HEALTH",HeartPulse,"Health","Individual & family medical cover"],["LIFE",LifeBuoy,"Life","Long-term income protection"],["VEHICLE",Car,"Vehicle","Private and commercial motor"]].map(([id,Icon,title,sub])=><button key={String(id)} className={type===id?"selected":""} onClick={()=>setType(String(id))}><Icon/><strong>{String(title)}</strong><span>{String(sub)}</span></button>)}</div></>}
      {step===2&&<><h3>Select the insurance provider</h3><p className="muted">Available providers are filtered for {type.toLowerCase()} policies.</p><div className="provider-grid">{Object.entries(companyLabel).filter(([k])=>type==="LIFE"?k==="LIC":type==="VEHICLE"?k.includes("VEHICLE")||k==="IFFCO_TOKIO":k.includes("HEALTH")).map(([k,v])=><button key={k}><Building2/><strong>{v}</strong><span>View eligible plans</span></button>)}</div></>}
      {step===3&&<><h3>Enter policy details</h3><div className="form-grid"><label className="wide">Customer<input placeholder="Search customer name or phone"/></label><label>Policy number<input placeholder="Policy number"/></label><label>Plan name<input placeholder="Select a plan"/></label><label>Start date<input type="date"/></label><label>End date<input type="date"/></label><label>Sum insured<input placeholder="₹ 0"/></label><label>Premium amount<input placeholder="₹ 0"/></label><label>Commission rate<input placeholder="0 %"/></label></div></>}
      {step===4&&<div className="review-state"><div className="review-icon"><ClipboardCheck/></div><h3>Ready to create this policy</h3><p>Review the entered details, then save. Renewal reminders and the applicable premium schedule will be created automatically.</p><div><span>Policy type <b>{type}</b></span><span>Reminder schedule <b>30 · 15 · 7 · 1 days</b></span></div></div>}
    </div>
    <div className="drawer-actions"><button className="button subtle" onClick={()=>step===1?close():setStep(step-1)}>{step===1?"Cancel":"Back"}</button><button className="button primary" onClick={()=>step===4?close():setStep(step+1)}>{step===4?"Create policy":"Continue"} <ChevronRight size={16}/></button></div>
  </div>;
}

function Vehicles() {
  return <><PageHeader eyebrow="Motor book" title="Vehicles" description="Vehicle documents, insurance status and PUC dates without the paper chase." action={<button className="button primary"><Plus size={17}/> Add vehicle</button>} />
    <div className="vehicle-grid">{vehicles.map((v,i)=><article className="vehicle-card" key={v.reg}><div className="vehicle-top"><div className={`vehicle-icon v${i}`}><Car/></div><span className={v.insurance.includes("soon")?"status warning":"status active"}>{v.insurance}</span></div><h3>{v.vehicle}</h3><div className="reg-number">{v.reg}</div><dl><div><dt>Owner</dt><dd>{v.owner}</dd></div><div><dt>Type</dt><dd>{v.type}</dd></div><div><dt>PUC expiry</dt><dd className={v.puc.includes("29 Jul")?"danger-text":""}>{v.puc}</dd></div></dl><button className="button subtle full">View vehicle <ChevronRight size={16}/></button></article>)}</div></>;
}

function Notifications() {
  const reminders=[
    ["Arjun Mehta","STH/25/018492","Expiry · 7 days","WhatsApp","Today, 9:00 AM","Scheduled"],
    ["Meera Nair","LIC/915/704821","Premium overdue","Email","Today, 9:05 AM","Sent"],
    ["Priya Raman","TAG/H/552109","Expiry · 30 days","WhatsApp","Yesterday","Sent"],
    ["Karthik Iyer","IFT/M/992670","PUC expiry","In-app","Yesterday","Unread"]
  ];
  return <><PageHeader eyebrow="Communication" title="Reminders" description="Automated, timely follow-ups across WhatsApp, email and in-app alerts." action={<button className="button subtle"><Settings size={17}/> Reminder settings</button>} />
    <section className="reminder-hero"><div><span className="eyebrow light">Renewal campaign</span><h2>18 policies expire in the next 30 days</h2><p>A personal reminder today could protect ₹6.2 lakh in renewal premium.</p></div><div><button className="button white"><Send size={17}/> Send WhatsApp to all</button><button className="button ghost-white">Preview message</button></div></section>
    <section className="card table-card"><div className="table-toolbar"><div className="tabs"><button className="active">All</button><button>Scheduled</button><button>Sent</button><button>Failed</button></div><button className="button subtle">All channels <ChevronDown size={15}/></button></div><div className="table-scroll"><table><thead><tr><th>Customer</th><th>Policy no.</th><th>Reminder</th><th>Channel</th><th>Scheduled</th><th>Status</th><th/></tr></thead><tbody>{reminders.map((r)=><tr key={r[1]}><td><div className="person"><span>{initials(r[0])}</span><strong>{r[0]}</strong></div></td><td>{r[1]}</td><td><strong>{r[2]}</strong></td><td>{r[3]}</td><td>{r[4]}</td><td><span className={`status ${r[5].toLowerCase()}`}>{r[5]}</span></td><td><button className="row-action"><MoreHorizontal/></button></td></tr>)}</tbody></table></div></section>
  </>;
}

function Reports() {
  const rows=[["Star Health","96","₹24.8L","₹3.12L","82%"],["Tata AIG Health","50","₹13.2L","₹1.54L","76%"],["LIC","82","₹35.6L","₹2.68L","91%"],["Tata AIG Vehicle","31","₹6.9L","₹86K","72%"],["IFFCO Tokio","27","₹5.8L","₹71K","68%"]];
  return <><PageHeader eyebrow="Business intelligence" title="Reports" description="Understand growth, commissions and renewal risk at a glance." action={<div className="header-actions"><button className="button subtle"><Download size={17}/> PDF</button><button className="button primary"><Download size={17}/> Export Excel</button></div>} />
    <div className="report-tabs"><button className="active">Commission</button><button>Monthly performance</button><button>Expiry list</button><button>Customer book</button></div>
    <div className="stats-grid report-stats"><Stat icon={CircleDollarSign} tone="green" label="Commission earned" value="₹8.91L" meta="July 2026"/><Stat icon={TrendingUp} tone="blue" label="Growth vs June" value="+14.2%" meta="₹1.11L increase"/><Stat icon={ClipboardCheck} tone="amber" label="Received" value="₹7.26L" meta="81.5% collected"/><Stat icon={CalendarClock} tone="violet" label="Pending" value="₹1.65L" meta="Across 38 policies"/></div>
    <section className="card table-card"><div className="table-toolbar"><div><h3>Commission by provider</h3><p>01 July – 26 July 2026</p></div><button className="button subtle"><CalendarClock size={16}/> This month <ChevronDown size={15}/></button></div><div className="table-scroll"><table><thead><tr><th>Provider</th><th>Policies</th><th>Total premium</th><th>Commission</th><th>Received</th><th>Collection</th></tr></thead><tbody>{rows.map((r,i)=><tr key={r[0]}><td><Company value={Object.keys(companyLabel)[i]}/></td><td><strong>{r[1]}</strong></td><td>{r[2]}</td><td><strong>{r[3]}</strong></td><td>{r[4]}</td><td><div className="progress"><i style={{width:r[4]}}/><span>{r[4]}</span></div></td></tr>)}</tbody></table></div></section>
  </>;
}

function Portal() {
  return <div className="portal-page"><section className="portal-hero"><div className="portal-brand"><ShieldCheck/> MyPolicy</div><div><span className="eyebrow light">Customer portal preview</span><h1>Hello, Arjun.</h1><p>Your family’s protection is in good shape. One policy needs your attention this week.</p></div></section><div className="portal-content"><div className="portal-summary"><Stat icon={ShieldCheck} tone="green" label="Active policies" value="3" meta="Health · Life · Vehicle"/><Stat icon={CalendarClock} tone="amber" label="Next renewal" value="7 days" meta="Star Health · 02 Aug"/><Stat icon={CircleDollarSign} tone="blue" label="Annual premium" value="₹86,400" meta="Across all policies"/></div><section className="card"><CardHead title="My policies" subtitle="Your active protection at a glance"/><div className="table-scroll"><table><thead><tr><th>Policy</th><th>Provider</th><th>Sum insured</th><th>Expiry</th><th>Status</th><th/></tr></thead><tbody>{policies.slice(0,3).map(p=><tr key={p.id}><td><strong>{p.plan}</strong><small>{p.number}</small></td><td><Company value={p.company}/></td><td>{inr(p.premium*15)}</td><td>{date(p.expiry)}</td><td><Days days={p.days}/></td><td><button className="button subtle compact">View details</button></td></tr>)}</tbody></table></div></section></div></div>;
}

function Stat({ icon: Icon, tone, label, value, meta }: { icon: typeof ShieldCheck; tone: string; label: string; value: string; meta: string }) {
  return <article className="stat card"><div className={`stat-icon ${tone}`}><Icon /></div><div><span>{label}</span><strong>{value}</strong><small>{meta}</small></div></article>;
}
function CardHead({title,subtitle,action}:{title:string;subtitle:string;action?:React.ReactNode}) { return <div className="card-head"><div><h3>{title}</h3><p>{subtitle}</p></div>{action}</div>; }
function Company({value}:{value:string}) { return <span className={`company ${value.toLowerCase()}`}><i/>{companyLabel[value]||value}</span>; }
function Days({days}:{days:number}) { return <span className={`days ${days<0?"expired":days<=7?"urgent":days<=15?"soon":"safe"}`}>{days<0?"Expired":days>30?"Active":`${days} days left`}</span>; }
function Follow({name,note,time,lead}:{name:string;note:string;time:string;lead:string}) { return <div className="follow"><div className="timeline-dot"/><div><strong>{name}</strong><p>{note}</p><span>{time} · <b>{lead}</b></span></div><button className="message-button"><Send/></button></div>; }
function Drawer({title,subtitle,close,wide,children}:{title:string;subtitle:string;close:()=>void;wide?:boolean;children:React.ReactNode}) { return <><button className="drawer-scrim" onClick={close}/><aside className={`drawer ${wide?"wide":""}`}><header><div><h2>{title}</h2><p>{subtitle}</p></div><button className="icon-button" onClick={close}><X/></button></header>{children}</aside></>; }
const initials=(name:string)=>name.split(" ").map(x=>x[0]).slice(0,2).join("");
const date=(value:string)=>new Intl.DateTimeFormat("en-IN",{day:"2-digit",month:"short",year:"numeric"}).format(new Date(value));
