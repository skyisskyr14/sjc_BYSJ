/*
  用法：
  BASE_URL=http://localhost:10438/api TASK_ID=1 RATE=200 DURATION=30 node scripts/load_track.js
*/
const BASE_URL = process.env.BASE_URL || 'http://localhost:10438/api'
const TASK_ID = Number(process.env.TASK_ID || 1)
const RATE = Number(process.env.RATE || 100)
const DURATION = Number(process.env.DURATION || 20)

async function postTrack(i){
  const body = {
    taskId: TASK_ID,
    longitude: 120.3 + Math.random()*0.2,
    latitude: 36.0 + Math.random()*0.2,
    ts: new Date().toISOString()
  }
  await fetch(`${BASE_URL}/sjc/dispatch/track/report`, {
    method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify(body)
  })
}

(async()=>{
  const total = RATE * DURATION
  const start = Date.now()
  let ok=0, fail=0
  for(let s=0;s<DURATION;s++){
    const jobs=[]
    for(let i=0;i<RATE;i++) jobs.push(postTrack(i).then(()=>ok++).catch(()=>fail++))
    await Promise.allSettled(jobs)
  }
  const cost=((Date.now()-start)/1000).toFixed(2)
  console.log(JSON.stringify({total,ok,fail,cost,rate: (ok/cost).toFixed(2)}, null, 2))
})()
