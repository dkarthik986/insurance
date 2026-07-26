export const customers = [
  { id:"1", name:"Arjun Mehta", phone:"+91 98765 12480", email:"arjun.mehta@example.com", city:"Chennai", policies:3, lead:"HOT", premium:86400 },
  { id:"2", name:"Priya Raman", phone:"+91 98401 77231", email:"priya.r@example.com", city:"Coimbatore", policies:2, lead:"WARM", premium:48900 },
  { id:"3", name:"Karthik Iyer", phone:"+91 98840 55671", email:"karthik.i@example.com", city:"Madurai", policies:1, lead:"WARM", premium:23750 },
  { id:"4", name:"Meera Nair", phone:"+91 99402 18643", email:"meera.nair@example.com", city:"Salem", policies:4, lead:"HOT", premium:112500 },
  { id:"5", name:"Vikram Shah", phone:"+91 97910 22580", email:"vikram.s@example.com", city:"Chennai", policies:1, lead:"COLD", premium:18400 }
];
export const policies = [
  { id:"p1", customer:"Arjun Mehta", number:"STH/25/018492", company:"STAR_HEALTH", plan:"Family Health Optima", type:"HEALTH", premium:28640, expiry:"2026-08-02", days:7, status:"ACTIVE" },
  { id:"p2", customer:"Meera Nair", number:"LIC/915/704821", company:"LIC", plan:"Jeevan Umang", type:"LIFE", premium:52300, expiry:"2026-08-09", days:14, status:"ACTIVE" },
  { id:"p3", customer:"Priya Raman", number:"TAG/H/552109", company:"TATA_AIG_HEALTH", plan:"Medicare Premier", type:"HEALTH", premium:34500, expiry:"2026-08-22", days:27, status:"ACTIVE" },
  { id:"p4", customer:"Karthik Iyer", number:"IFT/M/992670", company:"IFFCO_TOKIO", plan:"Private Car Protect", type:"VEHICLE", premium:18750, expiry:"2026-09-18", days:54, status:"ACTIVE" },
  { id:"p5", customer:"Vikram Shah", number:"TAG/V/771026", company:"TATA_AIG_VEHICLE", plan:"Auto Secure", type:"VEHICLE", premium:18400, expiry:"2026-07-21", days:-5, status:"EXPIRED" }
];
export const vehicles = [
  { reg:"TN 09 CW 4821", vehicle:"Hyundai Creta · 2023", owner:"Arjun Mehta", type:"CAR", insurance:"Active", puc:"18 Sep 2026" },
  { reg:"TN 38 BK 9012", vehicle:"Honda Activa 6G · 2022", owner:"Priya Raman", type:"BIKE", insurance:"Active", puc:"02 Aug 2026" },
  { reg:"TN 11 AZ 7864", vehicle:"Tata Nexon EV · 2024", owner:"Meera Nair", type:"CAR", insurance:"Active", puc:"Not required" },
  { reg:"TN 58 AM 1049", vehicle:"Maruti Baleno · 2021", owner:"Karthik Iyer", type:"CAR", insurance:"Expires soon", puc:"29 Jul 2026" }
];
export const revenue = [
  { month:"Feb", premium:3.8, commission:.54 },{ month:"Mar", premium:4.3, commission:.62 },
  { month:"Apr", premium:4.0, commission:.58 },{ month:"May", premium:5.1, commission:.76 },
  { month:"Jun", premium:4.8, commission:.71 },{ month:"Jul", premium:5.7, commission:.86 }
];
export const companyLabel: Record<string,string> = { STAR_HEALTH:"Star Health",TATA_AIG_HEALTH:"Tata AIG Health",LIC:"LIC",TATA_AIG_VEHICLE:"Tata AIG Vehicle",IFFCO_TOKIO:"IFFCO Tokio" };
export const inr=(n:number)=>new Intl.NumberFormat("en-IN",{style:"currency",currency:"INR",maximumFractionDigits:0}).format(n);
